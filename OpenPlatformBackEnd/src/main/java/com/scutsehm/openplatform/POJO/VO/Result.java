package com.scutsehm.openplatform.POJO.VO;

import lombok.Data;

/**
 * 返回结果时的结果类
 * 可以使用 {@code Result.OK().msg("success").data(data).build();} 的流式构建方式
 */
@Data
public class Result<E> {
    private String status;
    private String msg;
    private E data;
    public Result(String status, String msg){
        this.status = status;
        this.msg = msg;
    }
    public Result(String status, String msg, E data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
    public Result(int status, String msg)
    {
        this(Integer.toString(status), msg);
    }
    public Result(int status, String msg, E data) {
        this(Integer.toString(status), msg, data);
    }


    /**
     * 建议用ResultBuilder的build方法来生成Result
     * 这样Result的状态能够维持一致性：
     * --> 要么没有Result，
     * --> 要么Result的状态是不可改变的 immutable
     * 可以用 {@code Result.OK().msg("success").data(data).build();} 的流式API方式来构建Result
     */
    public static class ResultBuilder<E>
    {
        private String status;
        private String msg;
        private E data;
        public ResultBuilder(String status, String msg) {
            this.status = status;
            this.msg = msg;
        }
        public ResultBuilder<E> msg(String msg) {
            this.msg = msg;
            return this;
        }
        public ResultBuilder<E> data(E data) {
            this.data = data;
            return this;
        }
        public Result<E> build() {
            return new Result<E>(status, msg, data);
        }
        public String getStatus()
        {
            return status;
        }
        public String getMsg()
        {
            return msg;
        }
        public Object getData()
        {
            return data;
        }
    }
    public static<E> ResultBuilder<E> OK() {return new ResultBuilder<E>( "200", "OK");};
    public static<E> ResultBuilder<E> BAD() {
        return new ResultBuilder<E>("500", "Bad Request");
    }
    public static<E> ResultBuilder<E> NOT_FOUND() {
        return new ResultBuilder<E>("404", "Resource Not Found");
    }

}
