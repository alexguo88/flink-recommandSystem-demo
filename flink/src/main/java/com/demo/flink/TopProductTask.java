package com.demo.flink;

import com.demo.flink.agg.CountAgg;
import com.demo.domain.LogEntity;
import com.demo.domain.TopProductEntity;
import com.demo.flink.map.TopProductMapFunction;
import com.demo.flink.sink.TopNRedisSink;
import com.demo.flink.process.TopNHotItems;
import com.demo.util.Property;
import com.demo.flink.window.WindowResultFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.util.Collector;

import java.util.List;
import java.util.Properties;

/**
 * 热门商品 -> redis
 */
public class TopProductTask {

    private static final int topSize = 5;

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime); // 开启EventTime

        //redis
        FlinkJedisPoolConfig conf = new FlinkJedisPoolConfig.Builder()
                .setHost(Property.getStrValue("redis.host"))
//				.setPort(Property.getIntValue("redis.port"))
//				.setDatabase(Property.getIntValue("redis.db"))
                .build();

        //kafka
        Properties properties = Property.getKafkaProperties("topProuct");
        DataStreamSource<String> dataStream = env.addSource(new FlinkKafkaConsumer<String>("con", new SimpleStringSchema(), properties));

        //
        DataStream<TopProductEntity> topProduct = dataStream
                .map(new TopProductMapFunction()).//
                assignTimestampsAndWatermarks(new AscendingTimestampExtractor<LogEntity>() { // 抽取时间戳做watermark 以 秒 为单位
                    @Override
                    public long extractAscendingTimestamp(LogEntity logEntity) {
                        return logEntity.getTime() * 1000;
                    }
                })
                .keyBy("productId").timeWindow(Time.seconds(60), Time.seconds(5)).aggregate(new CountAgg(), new WindowResultFunction()) //按产品id，5秒时间窗，计数
                .keyBy("windowEnd").process(new TopNHotItems(topSize)).flatMap(new FlatMapFunction<List<String>, TopProductEntity>() {//取前5个，
                    @Override
                    public void flatMap(List<String> strings, Collector<TopProductEntity> collector) throws Exception {
                        System.out.println("-------------Top N Product------------");

                        for (int i = 0; i < strings.size(); i++) {
                            TopProductEntity top = new TopProductEntity();
                            top.setRankName(String.valueOf(i));
                            top.setProductId(Integer.parseInt(strings.get(i)));


                            //System.out.println(top); // 输出排名结果
                            collector.collect(top);
                        }

                    }
                });

        //存储结果
        topProduct.addSink(new RedisSink<>(conf, new TopNRedisSink()));

        env.execute("Top N ");
    }
}
