package com.jcohy.sample.reactive.chapter_04;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;

import com.jcohy.sample.reactive.chapter_04.dto.Connection;
import com.jcohy.sample.reactive.chapter_04.dto.Transaction;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:17:09
 * @since 1.0.0
 */
public class ReactorFluxTest {
    private static final Logger log = LoggerFactory.getLogger(ReactorFluxTest.class);
    private final Random random = new Random();


    @Test
    @Disabled
    public void endlessStream() {
        Flux.interval(Duration.ofMillis(1))
                .collectList()
                .block();
    }

    @Test
    @Disabled
    public void endlessStream2() {
        Flux.range(1,5)
                .repeat()
                .doOnNext(e -> log.info("E: {}",e))
                .take(100)
                .blockLast();
    }

    /**
     * 尝试收集无限流发出的所有元素可能导致 OutOfMemoryException
     *
     * range 操作符创建从 1 到 100 的整数序列
     * repeat 操作符在源流完成之后一次又一次地订阅源响应式流。
     * collectList 尝试将所有生成的元素收集到一个列表中。
     * block 操作符会触发实际订阅并阻塞正在运行的线程，直到最终结果到达，而在当前场
     * 景下不会发生这种情况，因为响应式流是无限的。
     */
    @Test
    @Disabled
    public void endlessStreamAndCauseAnError() {
        Flux.range(1, 100)
                .repeat()
                .collectList()
                .block();
    }

    /**
     * 创建 Flux 的方法
     */
    @Test
    public void createFlux() {
        Flux<String> stream1 = Flux.just("Hello", "world");
        Flux<Integer> stream2 = Flux.fromArray(new Integer[]{1, 2, 3});
        Flux<Integer> stream3 = Flux.range(1, 500);

        Flux<String> emptyStream = Flux.empty();
        Flux<String> streamWithError = Flux.error(new RuntimeException("Hi!"));

        Flux<String> empty = Flux.empty();
    }


    /**
     * 们创建一个简单的响应式流并订阅它:
     */
    @Test
    public void simpleSubscribe(){
        Flux.just("A","B","C")
                .subscribe(
                        (data) -> log.info("onNext: {}",data),
                        errorIgnored -> {},
                        () -> log.info("onComplete"));
    }

    /**
     * 创建一个 range 响应式流，并手动订阅
     * 首先请求 4 个数据，然后立即取消订阅
     */
    @Test
    public void simpleRangeSubscribe(){
        Flux.range(1,100)
                .subscribe(
                        (data) -> log.info("onNext: {}",data),
                        errorIgnored -> {},
                        () -> log.info("onComplete"),
                        subscription -> {
                            subscription.request(4);
                            subscription.cancel();
                        });
    }

    @Test
    public void simpleRange() {
        Flux.range(2019,9)
                .subscribe( y -> System.out.print(y + ","));
    }
    /**
     * 使用 Disposable 取消订阅
     * @throws InterruptedException /
     */
    @Test
    public void managingSubscription() throws InterruptedException {
        Disposable disposable = Flux.interval(Duration.ofMillis(50))
                .doOnCancel(() -> log.info("Cancelled"))
                .subscribe(
                        (data) -> log.info("onNext: {}", data));
        Thread.sleep(200);
        disposable.dispose();
    }

    /**
     * 实现自定义的 Subscriber
     *
     * 这个测试类的定义订阅的方法是不对的。它打破了线性代码流，也容易出错。最困难的部分是
     * 我们需要自己管理背压并正确实现订阅者的所有 TCK 要求。此外，在此示例中，我们打破了有关订阅验证和取消这几个 TCK 要求。
     *
     *  我们建议扩展 Project Reactor 提供的 BaseSubscriber 类。{@link MySubscriber}
     */
    @Test
    public void subscribingOnStream() throws InterruptedException {
        Subscriber<String> subscriber = new Subscriber<String>() {

            // 我们的订阅者必须持有对 Subscription 的引用，而 Subscription 需要绑定 Publisher
            // 和 Subscriber 。由于订阅和数据处理可能发生在不同的线程中，因此我们使用 volatile 关键
            // 字来确保所有线程都具有对 Subscription 实例的正确引用。
            volatile Subscription subscription;

            /**
             * 订阅到达时，通过 onSubscribe 回调通知 Subscriber 。在这里，我们保存订阅并初始化请求需求。
             * 如果没有该请求，与 TCK 兼容的提供者将不会发送数据，并且根本不会开始处理元素。
             * @param s
             */
            @Override
            public void onSubscribe(Subscription s) {
                subscription = s;
                log.info("initial request for 1 element");
                subscription.request(1);
            }

            /**
             * 在 onNext 回调中，记录接收的数据并请求下一个元素。在这种情况下，我们使用简单的
             * 拉模型 (subscription.request(1) )来管理背压。
             * @param s
             */
            @Override
            public void onNext(String s) {
                log.info("onNext: {}", s);
                log.info("requesting 1 more element");
                subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        };

        // 用 just 工厂方法生成一个简单的流。将自定义订阅者订阅到定义的响应式流。
        Flux.just("Hello", "world", "!")
                .subscribe(subscriber);

        Thread.sleep(100);
    }

    @Test
    public void mySubscriber() {
        Flux.just("A", "B", "C")
                .subscribe(new MySubscriber<>());
    }

    // ================================== 映射响应式序列元素 ==================================
    /**
     * index 操作符可用于枚举序列中的元素。
     *
     * timestamp 操作符的行为与 index 操作符类似，但会添加当前时间戳而不是索引。
     *
     */
    @Test
    public void indexElements() {
        Flux.range(2018,5)
                // 使用 timestamp 操作符添加当前时间戳。现在， 序列具有 Flux<Tuple2<Long,Integer>> 类型。
                .timestamp()
                // 使用 index 操作符实现枚举。现在， 序列具有 Flux<Tuple2<Long,Tuple2<Long,Integer>>> 类型。
                .index()
                .subscribe(
                        e -> log.info("index : {}, ts: {}, value: {}",
                        e.getT1(),
                        Instant.ofEpochMilli(e.getT2().getT1()),
                        e.getT2().getT2()));
    }

    // ================================== 过滤响应式序列元素 ==================================
    // filter 操作符仅传递满足条件的元素。
    // ignoreElements 操作符返回 Mono<T> 并过滤所有元素。结果序列仅在原始序列结束后结束。
    // 该库能使用 take(n) 方法限制所获取的元素，该方法忽略除前 n 个元素之外的所有元素。
    // takeLast 仅返回流的最后一个元素。
    // takeUntil(Predicate) 传递一个元素直到满足某个条件。
    // elementAt( n ) 只可用于获取序列的第 n 个元素。
    // Single 操作符从数据源发出单个数据项，也为空数据源发出 NoSuchElementException 错误信号，或者为具有多个元素的数据源发出 IndexOutOfBoundsException 信号。它不
    //      仅可以基于一定数量来获取或跳过元素，还可以通过带有 Duration 的 skip(Duration) 或 take(Duration) 操作符。
    // takeUntilOther(Publisher) 或 skipUntilOther(Publisher) 操作符， 可以获取或跳过一个元素，直到某些消息从另一个流到达。

    /**
     * 在启动-停止命令之间查看元素
     * @throws InterruptedException /
     */
    @Test
    public void startStopStreamProcessing() throws InterruptedException {
        Mono<Long> startCommand = Mono.delay(Duration.ofSeconds(1));
        Mono<Long> stopCommand = Mono.delay(Duration.ofSeconds(3));

        Flux<Long> streamData = Flux.interval(Duration.ofMillis(100));

        streamData
                .skipUntilOther(startCommand)
                .takeUntilOther(stopCommand)
                .subscribe(System.out::println);

        Thread.sleep(4000);
    }

    // ================================== 收集响应式序列元素 ==================================
    // collectList()
    // collectSortedList
    // collectMap: 操作符的映射( Map<K,T> );
    // collectMultimap: 操作符的多映射( Map<K,Collection<T>> );
    // defaultIfEmpty
    // distinct:
    // distinctUntilChanged():可用于无限流以删除出现在不间断行中的重复项

    @Test
    public void collectSort(){
        Flux.just(1, 6, 2, 8, 3, 1, 5, 1)
                .collectSortedList(Comparator.reverseOrder())
                .subscribe(System.out::println);
    }

    @Test
    public void distinctUntilChanged(){
        Flux.just(1, 6, 2, 8, 3, 1, 5, 1)
                .distinct()
                .subscribe(System.out::println);
    }

    // ================================== 裁剪响应式序列元素 ==================================
    // any(Predicate): 操作符检查是否至少有一个元素具有所需属性
    // hasElements 操作符检查流中是否包含多个元素
    // hasElement 操作符检查流中是否包含某个所需的元素。
    // sort: 排序
    // reduce: reduce 操作符通常需要一个初始值和一个函数，而该函数会将前一步的结果与当前步的元素组合在一起
    // scan: 与 reduce 类似，但他可以发出中间结果
    // then, thenMany, thenEmpty 这些操作符忽略传人元素，仅重放完成或错误信号。上游流完成处理后，这些操作符可用于触发新流

    /**
     * 检查元素中是否包含偶数
     */
    @Test
    public void findingIfThereIsEvenElements() {
        Flux.just(3,5,6,7,11,15,16,17)
                .any( e -> e % 2 == 0 )
                .subscribe(hasEvens -> log.info("Has evens: {}", hasEvens));
    }

    @Test
    public void reduceExample() {
        Flux.range(1,5)
                .reduce(0,(acc,elem) -> acc + elem)
                .subscribe(result -> log.info("Result: {}", result));
    }

    @Test
    public void scanExample() {
        Flux.range(1,5)
                .scan(0,(acc,elem) -> acc + elem)
                .subscribe(result -> log.info("Result: {}", result));
    }

    /**
     * 计算流中的移动平均值
     */
    @Test
    public void runningAverageExample() {
        // 移动平均窗口的大小
        int bucketSize = 5;
        Flux.range(1, 500)
                // 为数据添加索引
                .index()
                // 使用 scan 操作符，将最新的 5 个元素收集到 int 数组中
                .scan(
                        new int[bucketSize],
                        (acc, elem) -> {
                            acc[(int) (elem.getT1() % bucketSize)] = elem.getT2();
                            return acc;
                        })
                // 跳过流开头的一些元素来收集计算移动平均所需的足够数据。
                .skip(bucketSize)
                // 为了计算移动平均值，将容器内容的总和除以其大小。
                .map(array -> Arrays.stream(array).sum() * 1.0 / bucketSize)
                .subscribe(av -> log.info("Running average: {}", av));
    }

    /**
     * 即使 1 、2 和 3 是由流生成和处理的. subscribe 方法中的 lambda 也只接收 4 和 5 。
     */
    @Test
    public void thenOperator() {
        Flux.just(1,2,3)
                .thenMany(Flux.just(4,5))
                .subscribe(e -> log.info("onNext: {}", e));
    }

    // ================================== 组合响应式序列元素 ==================================
    // concat: 通过向下游转发接收的元素来连接所有数据源。当操作符连接两个流时，它首先消费并重新发送第一个流的所有元素，然后对第二个流执行相同的操作。
    // merge: 将来自上游序列的数据合并到一个下游序列中。与 concat 操作符不间，上游数据源是立即(同时)被订阅的。
    // zip: 订阅所有上游，等待所有数据源发出一个元素，然后将接收到的元素组合到一个输出元素中。第 2 章详细描述了 zip 的工作原理。在 Reactor 中，zip 操作符不仅
    //      可以与响应式发布者一起运行，还可以与 Iterable 容器一起运行。针对后者， 我们可以使用 zipWithIterable 操作符。
    // combineLatest: 与zip 操作符的工作方式类似。但是，只要至少一个上游数据源发出一个值，它就会生成一个新值。

    @Test
    public void combineLatestOperator() {
        Flux.concat(
                Flux.just(1,3),
                Flux.just(4,2),
                Flux.just(6,5)
        ).subscribe(e -> log.info("onNext: {}", e));
    }

    // ================================== 流元素批处理 ==================================
    // buffer：将元素缓冲( buffering )到容器(如 List) 中，结果流的类型为 Flux<List<T>>。
    // window: 通过开窗( windowing ) 方式将元素加入诸如 Flux<Flux<T>> 等流中。请注意，现在的流信号不是值，而是我们可以处理的子流。
    // groupBy: 分组

    /**
     * 列表(大小为 4) 中的整数元素执行缓冲操作
     */
    @Test
    public void bufferBySize() {
        Flux.range(1, 13)
                .buffer(4)
                .subscribe(e -> log.info("onNext: {}", e));
        /*
        result:
            onNext: [1, 2, 3, 4]
            onNext: [5, 6, 7, 8]
            onNext: [9, 10, 11, 12]
            onNext: [13]
         */
    }

    /**
     * 根据数字序列中的元素是否为索数进行开窗拆分
     */
    @Test
    public void windowByPredicate() {
        Flux<Flux<Integer>> fluxFlux = Flux.range(101, 20)
                .windowUntil(this::isPrime, true);

        fluxFlux.subscribe(window -> window
                .collectList()
                .subscribe(e -> log.info("window: {}", e)));

                /*
        result:
            window: []
            window: [101, 102]
            window: [103, 104, 105, 106]
            window: [107, 108]
            window: [109, 110, 111, 112]
            window: [113, 114, 115, 116, 117, 118, 119, 120]
         */
    }

    /**
     * 我们将整数序列按照奇数和偶数进行分组，并仅跟踪每组中的最后两个元素
     */
    @Test
    public void groupByExample(){
        Flux.range(1,7)
                // 使用groupBy 操作符，根据取模操作对序列进行奇数和偶数的拆分。操作符返回 Flux<GroupedFlux<String,Integer>> 类型的流。
                .groupBy( e -> e % 2 == 0 ? "Even":"Odd")
                // 订阅主 Flux ，而对于每个分组的 flux 元素，应用 scan 操作符。
                .subscribe(groupFlux -> groupFlux
                        // scan 操作符是具有空列表的种子。分组 flux 中的每个元索都会添加到列表中，如果列表大于两个元素， 则删除最早的元素。
                        .scan(
                                new LinkedList<>(),
                                (list,item) -> {
                                    if(list.size() > 1 ){
                                        list.remove(0);
                                    }
                                    list.add(item);
                                    return list;
                                }
                        )
                        // scan 操作符首先传播种子，然后重新计算值。在这种情况下， filter 操作符使我们能从扫描的种子中删除空数据容器。
                        .filter(arr -> !arr.isEmpty())
                        // 最后，单独订阅每个分组 flux 并显示 scan 操作符发送的内容
                        .subscribe(data ->
                                log.info("{}: {}",
                                        groupFlux.key(),
                                        data)));
    }

    @Test
    public void flatMapExample() throws InterruptedException {
        Flux.just("user-1", "user-2", "user-3")
                .flatMap(u -> requestBooks(u)
                        .map(b -> u + "/" + b))
                .subscribe(r -> log.info("onNext: {}", r));

        Thread.sleep(1000);
    }

    public Flux<String> requestBooks(String user){
        return Flux.range(1,random.nextInt(3) + 1)
                .delayElements(Duration.ofMillis(3))
                .map( i -> "Book" + i);
    }

    public boolean isPrime(int number) {
        return number > 2
                && IntStream.rangeClosed(2, (int) Math.sqrt(number))
                .noneMatch(n -> (number % n == 0));
    }

    // ================================== 元素采样 ==================================

    @Test
    public void sample() throws InterruptedException {
        Flux.range(1,100)
                .delayElements(Duration.ofMillis(1))
                .sample(Duration.ofMillis(20))
                .subscribe(r -> log.info("onNext: {}", r));
        Thread.sleep(1000);
    }

    // ================================== 将响应式序列转化为阻塞结构 ==================================
    // toIterable: 方法将响应式 Flux 转换为阻塞 Iterable 。
    // toStream: 方法将响应式 Flux 转换为阻塞 StreamAPI。从 Reactor 3.2 开始， 在底层上它使用了 toIterable 方法。
    // blockFirst: 方法阻塞了当前线程，直到上游发出第→个值或完成流为止。
    // blockLast: 方法阻塞当前线程， 直到上游发出最后一个值或完成流为止。在 onError 的情况下，它会在被阻塞的线程中抛出异常。

    // ================================== 在序列处理时查看元素 ==================================
    // doOnNext(Consumer<? super T> onNext): 使我们能对 Flux 或 Mono 上的每个元素执行一些操作。
    // doOnComplete() 和 doOnError(): 使我们能对 Flux 或 Mono 上的每个元素执行一些操作。
    // doOnSubscribe(Consumer<Subscription>)、doOnRequest(LongConsumer) 和 doOnCancel(Runnable) 使我们能对订阅生命周期事件做出响应。
    // doOnTerminate(Runnable): 无论是什么原因导致的流终止.，都会在流终止时被调用
    // doOnEach: 该方法处理表示响应式流领域的所有信号，包括 onError 、onSubscribe 、onNext 、onError 和 doOnComplete 。

    @Test
    public void doOnExample(){
        Flux.just(1,2,3)
                .concatWith(Flux.error(new RuntimeException("Conn error")))
                .doOnEach( s -> log.info("signal: {}", s))
                .subscribe();
    }

    // ================================== 物化和非物化信号 ==================================
    // materialize() :
    // dematerialize() :
    @Test
    public void signalProcessing() {
        Flux.range(1,3)
                .doOnNext( e -> System.out.println("data :" + e))
                .materialize()
                .doOnNext( e -> System.out.println("signal: " + e))
                .dematerialize()
                .collectList()
                .subscribe( r -> System.out.println("result: " + r));
    }

    @Test
    public void signalProcessingWithLog() {
        Flux.range(1,3)
                .log("FluxEvents")
                .subscribe( e -> {}, e -> {}, () -> {}, s -> s.request(2));
    }

    // ================================== 以编程的方式创建流 ==================================
    // push(): push 工厂方法能通过适配一个单线程生产者来编程创建F1ux 实例。此方法对于适配异步、
    //      单线程、多值 API 非常有用， 而无须关注背压和取消。如果订阅者无法处理负载， 则队列中的信号将会涵盖这两个方面。
    // create(): 其行为与 push 工厂方法类似。但是，该方法能从不同的线程发送事件，因为它还会序列化 FluxSink 实例
    // 这两种方法都支持重载溢出策略，并通过注册额外的处理程序来启用资源消理，如下面的代码所示:
    // generate 工厂方法: generate 工厂方法旨在基于生成器的内部处理状态创建复杂序列。它需要一个初始值和一个函数，
    //          该函数根据前一个内部状态计算下一个状态， 并将 onNext 信号发送给下游订阅者。
    // using: using 工厂方法能根据一个 disposable 资源创建流。它在响应式编程中实现了 try-with-resources 方法
    // usingWhen: 与 using 操作符类似， usingWhen 操作符使我们能以响应式方式管理资源。但是， using
    //          操作符会同步获取受托管资源(通过调用 Callable 实例)。同时. usingWhen 操作符响应式地获取受托管资源(通过订阅 Publisher 的实例)。
    //          此外.usingWhen 操作符接受不同的处理程序，以便应对主处理流终止的成功和失败。这些处理程序由发布者实现。这种区别使我们可以仅
    //          使用一个操作符实现完全无阻塞的响应式事务。

    /**
     * 使用 push 工厂方法使一些现有的 API 适配响应式范式。为简单起见，这里我们使用 Java Stream APl 生成 1000 个整数元素并将它们发送到
     * FluxSink 类型的 emitter 对象。在 push 方法中，我们不关心背压和取消， 因为 push 方法本身涵盖了这些功能。
     * @throws InterruptedException /
     */
    @Test
    public void usingPushOperator() throws InterruptedException {
        Flux.push( emitter -> IntStream
                .range(2000,3000)
                .forEach(emitter::next))
                // 延迟流中的每个元素来模拟背压情况。
                .delayElements(Duration.ofMillis(1))
                .subscribe(e -> log.info("onNext: {}",e));

        Thread.sleep(1000);
    }

    @Test
    public void usingCreateOperator() throws InterruptedException {
        Flux.create( emitter -> {
           emitter.onDispose(() -> log.info("Disposed"));
            // 将事件推送到发射器
        })
                .subscribe(e -> log.info("onNext: {}",e));
        Thread.sleep(1000);
    }

    @Test
    public void usingGenerate() throws InterruptedException {
        Flux.generate(
                // Tuples.of(OL,1L) 作为序列的初始状态
                () -> Tuples.of(0L,1L),
                // 在生成步骤中，我们通过引用状态对，并根据斐波那契序列中的下一个值重新计算新的状态对。
                (state,sink) -> {
                    log.info("generated value: {}", state.getT2());
                    sink.next(state.getT2());
                    long newValue = state.getT1() + state.getT2();
                    return Tuples.of(state.getT2(),newValue);
                })
                // 使用 delayElements 操作符在 onNext 信号之间引入一些延迟。
                .delayElements(Duration.ofMillis(1))
                // 获取前 7 个元素
                .take(7)
                .subscribe(e -> log.info("onNext: {}", e));

        Thread.sleep(100);
    }

    @Test
    public void tryWithResources() {
        try (Connection conn = Connection.newConnection()) {
            conn.getData().forEach(
                    data -> log.info("Received data: {}", data)
            );
        } catch (Exception e) {
            log.info("Error: {}", e.getMessage());
        }
    }

    @Test
    public void usingOperator() {
        Flux<String> ioRequestResults = Flux.using(
                Connection::newConnection,
                connection -> Flux.fromIterable(connection.getData()),
                Connection::close
        );

        ioRequestResults
                .subscribe(
                        data -> log.info("Received data: {}", data),
                        e -> log.info("Error: {}", e.getMessage()),
                        () -> log.info("Stream finished"));
    }

    /**
     * 我们可以使用 usingWhen 操作符实现一个更新的事务
     * @throws InterruptedException /
     */
    @Test
    public void usingWhenExample() throws InterruptedException {
        Flux.usingWhen(
                // beginTransaction 静态方法通过返回 Mono<Transaction> 类型异步返回一个新事务
                Transaction.beginTransaction(),
                // 对于给定的事务实例，它会尝试插入新行
                transaction -> transaction.insertRows(Flux.just("A","B")),
                // 如果步骤上一步成功完成，则提交事务。
                Transaction::commit,
                // 如果步骤上一步失败，则回滚事务。
                Transaction::rollback
        ).subscribe(
                d -> log.info("onNext: {}", d),
                e -> log.info("onError: {}", e.getMessage()),
                () -> log.info("onComplete")
        );

        Thread.sleep(1000);
    }

    // ================================== 错误处理 ==================================
    // onError():
    // onErrorReturn(): 操作符来捕获一个错误，并用一个默认静态值或一个从异常中计算出的值替换它。
    // onErrorResume(): 操作符米捕获异常并执行备用工作流。
    // onErrorMap(): 操作符来捕获异常并将其转换为另一个异常来更好地表现当前场景。
    // retry(): 在发生错误时重新执行的响应式工作流。
    // retryBackoff(): 操作符为指数退避算法(exponential backoff algorithm ) 提供开箱既用的支持，该算法会基于递增的延迟时间来重试该操作。
    // defaultIfEmpty(): 如果为空，可以指定默认值
    // switchIfEmpty(): 如果为空，可以返回完全不同的响应式流。

    @Test
    public void handlingErrors() throws InterruptedException {
        // 生成一个用户流， 这些用户会请求自己的电影推荐。
        Flux.just("user-1")
                .flatMap(user ->
                        // 针对每个用户， 调用不可靠的 recommendedBooks 服务
                        recommendedBooks(user)
                                // 如果调用失败，将以指数退避重试(不超过 5 次重试， 从 100 毫秒的持续时间开始)
                                .retryBackoff(5, Duration.ofMillis(100))
                                // 但是，如果重试策略在 3 秒钟后没有带来任何结果，则会触发一个错误信号
                                .timeout(Duration.ofSeconds(3))
                                // 最后， 如果出现任何错误，使用 onErrorResume 操作符返回预定义的通用推荐集
                                .onErrorResume(e -> Flux.just("The Martian"))
                )
                .subscribe(
                        b -> log.info("onNext: {}", b),
                        e -> log.warn("onError: {}", e.getMessage()),
                        () -> log.info("onComplete")
                );

        Thread.sleep(5000);
    }

    public Flux<String> recommendedBooks(String userId) {
        // 推迟计算，直到订阅者到达。
        return Flux.defer(() -> {
            if (random.nextInt(10) < 7) {
                return Flux.<String>error(new RuntimeException("Conn error"))
                        // 我们的不可靠服务很可能返回错误。但是，我们可以通过应用 delaySequence 操作符来及时转移所有信号。
                        .delaySequence(Duration.ofMillis(100));
            } else {
                return Flux.just("Blue Mars", "The Expanse")
                        // 如果客户端很幸运， 它们会收到一些延迟了的推荐。
                        .delayElements(Duration.ofMillis(50));
            }
            // 将每个请求记录到服务中。
        }).doOnSubscribe(s -> log.info("Request for {}", userId));
    }


    // ================================== 背压处理 ==================================
    // onBackPressureBuffer: 操作符会请求无界需求并将返回的元素推送到下游。但是， 如果下游消费者无法跟上，那么元素将缓冲在队列中。
    //                       onBackPressureBuffer 操作符有许多重载并公开了许多配置选项，这有助于调整其行为。
    // onBackPressureDrop: 操作符也请求无界需求(Integer.MAX_VALUE) 并向下游推送数据。
    //                      如果下游请求数量不足， 那么元素会被丢弃。自定义处理程序可以用来处理已丢弃的元素。
    // onBackPressureLast: 操作符与 onBackPressureDrop 的工作方式类似。但是，它会记住最近收到的元素，并在需求出现时立即将其推向下游。
    //                      即使在溢出情况下，始终接收最新数据。
    // onBackPressureError: 操作符在尝试向下游惟送数据时请求无界需求。如果下游消费者无法跟上，那么操作符会引发错误。
    // limitRate(n): 操作符会限制来自下游消费者的需求(总请求值)。例如， limitRequest(100)
    //              确保不会向生产者请求超过 100 个元素。发送 100 个事件后，操作符会成功关闭流。


    // ================================== 热数据流和冷数据流 ==================================
    // 冷发布者的行为方式是这样的. 无论订阅者何时出现， 都为该订阅者生成所有序列数据。此外， 对于冷发布者而言， 没有订阅者就不会生成数据
    // 热发布者中的数据生成不依赖于订阅者而存在。因此，热发布者可能在第一个订阅者出现之前开始生成元素。。此外，当订阅者出现时， 热发布者可能不会发送先前生成的值，而只发送新的值。这种语义代表数据广播场景。
    // just 工厂方法会生成一个热发布者.我们可以通过将 just 包装在 defer 中来将其转换为冷发布者。

    @Test
    public void coldPublisher() {
        Flux<String> coldPublisher = Flux.defer(() -> {
            log.info("Generating new items");
            return Flux.just(UUID.randomUUID().toString());
        });

        log.info("No data was generated so far");
        coldPublisher.subscribe(e -> log.info("onNext: {}", e));
        coldPublisher.subscribe(e -> log.info("onNext: {}", e));
        log.info("Data was generated twice for two subscribers");
    }

    /**
     * 多播流元素
     * 使用 ConnectableFlux 不仅可以生成数据以满足最急迫的需求， 还会缓存数据，以便所有其他订阅者可以按照自己的速
     * 度处理数据。
     *
     * 此示例冷发布者收到了订阅，只生成了一次数据项。但是，两个订阅者都收到了整个事件集合。
     */
    @Test
    public void connectExample() {
        Flux<Integer> source = Flux.range(0, 3)
                .doOnSubscribe(s ->
                        log.info("new subscription for the cold publisher"));

        ConnectableFlux<Integer> conn = source.publish();

        conn.subscribe(e -> log.info("[Subscriber 1] onNext: {}", e));
        conn.subscribe(e -> log.info("[Subscriber 2] onNext: {}", e));

        log.info("all subscribers are ready, connecting");
        conn.connect();

    }

    /**
     * 缓存流元素
     * 使用 ConnectableFlux 可以轻松实现不同的数据缓存策略。但是， Reactor 已经以 cache
     * 操作符的形式提供了用于事件缓存的 API。在内部， cache 操作符使用 ConnectableFlux， 因
     * 此它的主要附加值是它所提供的一个流式而直接的 API 。我们可以调整缓存所能容纳的数据量以及每个缓存项的到期时间。
     */
    @Test
    public void cachingExample() throws InterruptedException {
        // 首先创建一个生成一些数据项的冷发布者。
        Flux<Integer> source = Flux.range(0, 2)
                .doOnSubscribe(s ->
                        log.info("new subscription for the cold publisher"));

        // 使用缓存操作符缓存冷发布者， 持续时间为1 秒。
        Flux<Integer> cachedSource = source.cache(Duration.ofSeconds(1));

        // 连接第一个订阅者。
        cachedSource.subscribe(e -> log.info("[S 1] onNext: {}", e));
        // 在第一个订阅者之后，紧接着连接第二个订阅者。
        cachedSource.subscribe(e -> log.info("[S 2] onNext: {}", e));

        // 等待一段时间以使缓存的数据过期
        Thread.sleep(1200);

        // 最后连接第三个订阅者。
        cachedSource.subscribe(e -> log.info("[S 3] onNext: {}", e));
    }


    /**
     * 共享流元素
     * 我们可以使用 ConnectableFlux 向几个订阅者多播事件。但是，我们需要等待订阅者出现
     * 才能开始处理。share 操作符可以将冷发布者转变为热发布者。该操作符会为每个新订阅者传播订阅者尚未错过的事件
     * @throws InterruptedException /
     */
    @Test
    public void replayExample() throws InterruptedException {
        Flux<Integer> source = Flux.range(0, 5)
                .delayElements(Duration.ofMillis(100))
                .doOnSubscribe(s ->
                        log.info("new subscription for the cold publisher"));

        Flux<Integer> cachedSource = source.share();

        cachedSource.subscribe(e -> log.info("[S 1] onNext: {}", e));
        Thread.sleep(400);
        cachedSource.subscribe(e -> log.info("[S 2] onNext: {}", e));

        Thread.sleep(1000);

        // 第一个订阅者从第一个事件开始接收， 而第二个订阅者错过了在其出现之前所产生的事件( S2 仅接收到事件 3 和事件 4)。
    }

    /**
     * 处理时间
     * elapsed 操作符测量与上一个事件的时间间隔。
     * @throws InterruptedException /
     */
    @Test
    public void elapsedExample() throws InterruptedException {
        Flux.range(0, 5)
                .delayElements(Duration.ofMillis(100))
                .elapsed()
                .subscribe(e -> log.info("Elapsed {} ms: {}", e.getT1(), e.getT2()));

        Thread.sleep(1000);
    }

    /**
     * 组合和转换响应式流
     */
    @Test
    public void transformExample() {
        // 使用 Function<Flux<String> , Flux<String>> 签名来定义 logUserInfo 函数
        Function<Flux<String>, Flux<String>> logUserInfo =
                stream -> stream
                        // 还会使用 index 操作符对传入事件进行额外的枚举
                        .index()
                        // 我们的函数会针对每个 onNext 信号记录有关用户的详细信息
                        .doOnNext(tp ->
                                log.info("[{}] User: {}", tp.getT1(), tp.getT2()))
                        // 传出流不包含任何有关枚举的信息，因为我们会使用 map(Tuple2::getT2) 调用将其删除。
                        .map(Tuple2::getT2);

        // 生成一些用户 ID
        Flux.range(1000, 3)
                .map(i -> "user-" + i)
                // 通过应用 transform 操作符嵌入了 logUserInfo 函数所定义的转换
                .transform(logUserInfo)
                .subscribe(e -> log.info("onNext: {}", e));

    }

    /**
     * 该操作符在每次订阅者到达时都会执行相同的流转换
     */
    @Test
    public void composeExample() {
        // 与前面的例子类似， 我们定义了一个转换函数。在这种情况下， 函数每次随机选择流转换的路径。两个路径的不同之处仅在于日志消息前缀。
        Function<Flux<String>, Flux<String>> logUserInfo = (stream) -> {
            if (random.nextBoolean()) {
                return stream
                        .doOnNext(e -> log.info("[path A] User: {}", e));
            } else {
                return stream
                        .doOnNext(e -> log.info("[path B] User: {}", e));
            }
        };

        // 创建了一个生成一些数据的发布者。
        Flux<String> publisher = Flux.just("1", "2")
                // 使用 compose 操作符，将 logUserInfo 函数嵌入到执行工作流程中。
                .compose(logUserInfo);

        // 为了观察不同订阅的不同行为，我们还执行了几次订阅。
        publisher.subscribe();
        publisher.subscribe();
    }
}
