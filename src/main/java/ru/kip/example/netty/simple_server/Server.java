package ru.kip.example.netty.simple_server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ru.kip.example.netty.simple_server.inbound.SimpleInboundHandler;

public class Server {

    private final int port;

    private final SimpleInboundHandler simpleInboundHandler = new SimpleInboundHandler();

    public Server(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        int port = 8000;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        new Server(port).runServer();
    }

    private void runServer() throws Exception {
        //создание 2-х бесконечных циклов, обрабатывающих поступающие данные

        //"босс" - принимает все входящие соединения.
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //"работник" - принимает данных из соединений.
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //вспомогательный класс для запуска сервера
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        try {
            //настройка сервера перед запуском
            serverBootstrap.group(bossGroup, workerGroup)               //фиксация циклов для обработки входящей информации
                    .channel(NioServerSocketChannel.class)              //используемый тип "каналов" - оберток над соединениями
                    .childHandler(new SimpleServerChannelInitializer());//класс, производящий первичную настройку каналов перед работой

            //запуск сервера на указанном порту
            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            //закрытие цикла работника
            workerGroup.shutdownGracefully();
            //закрытие цикла босса
            bossGroup.shutdownGracefully();
        }
    }

    private class SimpleServerChannelInitializer extends ChannelInitializer {
        @Override
        protected void initChannel(Channel channel) {
            //добавление нового обработчика в цепочку обработчиков канала
            channel.pipeline().addLast(simpleInboundHandler);
        }
    }
}
