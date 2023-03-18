package github.javaguide.netty.kyro.codec;

import github.javaguide.netty.kyro.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 自定义解码器，从ByteBuf中获取到业务对象对应的字节序列，然后将字节序列转换为业务对象
 * @createTime 2023年3月12日 20点56分
 */
@AllArgsConstructor
@Slf4j
public class NettyKryoDecoder extends ByteToMessageDecoder {
    private final Serializer serializer;
    private final Class<?> genericClass;

    /**
     * Netty传输的消息长度也就是对象序列化后对应的字节数组的大小，ByteBuf有对应的记录长度的参数
     */
    private static final int BODY_LENGTH = 4;

    /**
     * 解码ByteBuf对象
     * @param ctx 解码器关联的 ChanytenelHandlerContext 对象
     * @param in "入站"数据，也就是BBuf对象
     * @param out 解码之后的数据对象需要添加到out对象里面
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 1、byteBuf中写入的消息长度所占的字节数已经是4了，所以byteBuf的可读字节必须大于4
        if(in.readableBytes() >= BODY_LENGTH){
            // 2、标记当前readIndex的位置，以便后面重置readIndex的时候使用
            in.markReaderIndex();
            // 3、读取消息的长度
            // 注意：消息长度是我们encode的时候写入的
            int dataLength = in.readInt();
            // 4、遇到不合理的情况直接 return
            if(dataLength < 0 || in.readableBytes() < 0){
                log.error("data length or byteBuf readableBytes is not valid");
                return;
            }
            // 5、如果可读字节数小于消息长度的话，说明是不完整的消息，重置readIndex
            if(in.readableBytes() < dataLength){
                in.resetReaderIndex();
                return;
            }
            // 6、走到这里说明没问题，可以反序列化了
            byte[] body = new byte[dataLength];
            in.readBytes(body);
            // 将bytes数组转化为我们需要的对象
            Object obj = serializer.deserialize(body, genericClass);
            out.add(obj);
            log.info("successful decode ByteBuf to Object");
        }
    }
}
