package com.example.demo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationRegex {
    public static boolean isRegexEmail(String target) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }
    public static boolean isRegexPassword(String target) { // 비밀번호 정규식 - 최소 8글자, 최대16글자, 대문자 1개, 소문자 1개, 숫자 1개, 특수문자 1개
        boolean result;
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$";
        result=target.matches(regex);
        return result;
    }
    public static boolean isRegexNickName(String target) { // 닉네임 정규식 - 2자 이상 16자 이하, 영어 또는 숫자 또는 한글로 구성
        String regex = "^(?=.*[a-z0-9가-힣])[a-z0-9가-힣]{2,10}$"; // * 특이사항 : 한글 초성 및 모음은 허가하지 않는다.
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }
}

