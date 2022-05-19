package cn.locusc.rpc.netty.api.domain;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class User implements Serializable {

    private int id;

    private String name;

}
