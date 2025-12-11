package pt.ulusofona.aed.deisimdb;

public class Result {

    boolean success;
    String error;
    String result;

    public Result(boolean success, String error, String result) {
        this.success = success;
        this.error = error;
        this.result = result;
    }



}
