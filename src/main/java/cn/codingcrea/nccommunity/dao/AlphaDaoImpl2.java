package cn.codingcrea.nccommunity.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary    //优先被注入
public class AlphaDaoImpl2 implements AlphaDao{
    @Override
    public String test() {
        return "testDao2";
    }
}
