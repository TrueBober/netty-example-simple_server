package ru.kip.example.netty.simple_server.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;

import java.nio.charset.Charset;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Реализация обработчика входящих данных.
 */
public class SimpleInboundHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = getLogger(SimpleInboundHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;

        //кол-во байтов, которые можно прочитать из буфера
        int readableBytes = buffer.readableBytes();
        //подготовка буфера для чтения
        byte[] data = new byte[readableBytes];
        //чтение данных в буфер
        buffer.readBytes(data);
        //преобразование данных в строку
        String inboundMessage = new String(data, Charset.forName("UTF-16"));
        log.info("Inbound message: {}", inboundMessage);
        //подготовить ответ к отправке
        ctx.write("Hello, " + inboundMessage);
        //выполнить отправку данных клиенту
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //закрыть контекст
        ctx.close();
        //логирование ошибки
        log.info("Error", cause);
    }
}
