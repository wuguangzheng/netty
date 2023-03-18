package github.javaguide.netty.kyro.codec;

import github.javaguide.netty.kyro.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

/**
 * 自定义编码器
 *
 * @createTime  2023年3月12日 19点30分
 */
@AllArgsConstructor
public class NettyKryoEncoder extends MessageToByteEncoder<Object> {
    private final Serializer serializer;
    private final Class<?> genericClass;

    /**
     * 将对象转换为字节码然后写入到ByteBuf对象中
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) {
        if(genericClass.isInstance(o)){
            // 1、将对象转换为字节
            byte[] body = serializer.serialize(o);
            // 2、读取消息的长度
            int dataLength = body.length;
            // 3、写入消息对应的字节数组长度， writerIndex 加 4，就是说这个参数为4个字节
            byteBuf.writeInt(dataLength);
            // 4、将字节数组写入 ByteBuf 对象中
            byteBuf.writeBytes(body);
        }
    }
}
