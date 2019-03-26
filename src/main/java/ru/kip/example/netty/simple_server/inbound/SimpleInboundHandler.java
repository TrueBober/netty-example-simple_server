package ru.kip.example.netty.simple_server.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;

import java.nio.charset.Charset;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Реализация обработчика входящих данных.
 */
@ChannelHandler.Sharable
public class SimpleInboundHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = getLogger(SimpleInboundHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;
        try {
            //кол-во байтов, которые можно прочитать из буфера
            int readableBytes = buffer.readableBytes();
            //подготовка буфера для чтения
            byte[] data = new byte[readableBytes];
            //чтение данных в буфер
            buffer.readBytes(data);
            //преобразование данных в строку
            String inboundMessage = new String(data, Charset.forName("UTF-8"));
            log.info("Inbound message: {}", inboundMessage);
            //подготовить ответ к отправке
            ctx.write(Unpooled.copiedBuffer("Hello, " + inboundMessage, Charset.forName("UTF-8")));
            //выполнить отправку данных клиенту
            ctx.flush();
        } finally {
            //т.к. буфер находится вне хипа, то для очистки неспользуемой памяти нужно уменьшить кол-во
            //ссылок на выделенный кусок памяти. Когда ссылок будет 0 - память очистится
            buffer.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //закрыть контекст
        ctx.close();
        //логирование ошибки
        log.info("Error", cause);
    }
}
