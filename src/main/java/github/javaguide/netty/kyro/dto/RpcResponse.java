package github.javaguide.netty.kyro.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RpcResponse {
    private String message;
}
