package cn.codingcrea.nccommunity.dao;

import org.springframework.stereotype.Repository;

@Repository
public class AlphaDaoImpl implements AlphaDao{

    @Override
    public String test() {
        return "testDao";
    }
}
