package com.foryou.gatewayservice.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GateWayUtils {
    public static List<Map<String, String>> changePathToListMap(String excludePath) {
        return Arrays.stream(excludePath.replaceAll(" ", "").split(";"))
                .filter(path -> !StringUtils.isEmpty(path))
                .map(path -> {
                    Map<String, String> map = new HashMap<>();

                    map.put("method", path.substring(0, path.indexOf("/")).toUpperCase());
                    map.put("regExUrl", changePathToRegEx(path));

                    return map;
                })
                .collect(Collectors.toList());
    }

    public static boolean isExcludePath(List<Map<String, String>> excludePathList, HttpMethod method, String path) {
        return excludePathList.stream()
                .anyMatch(map -> method.matches(map.get("method")) && Pattern.matches(map.get("regExUrl"), path));
    }

    public static boolean isExistMemberIdInPath(String memberId, String path) {
        return Arrays.stream(path.split("/"))
                .anyMatch(splitPath -> splitPath.equals(memberId));
    }

    private static String changePathToRegEx(String path) {
        String regExPath = Arrays.stream(path.substring(path.indexOf("/")).split("/"))
                .filter(slice -> !StringUtils.isEmpty(slice))
                .map(slice -> {
                    StringBuilder sb = new StringBuilder();

                    sb.append("\\/");
                    sb.append(slice.equals("**") ? "([^\\/]*)" : slice);

                    return sb.toString();
                })
                .collect(Collectors.joining());

        return "^" + regExPath + "$";
    }
}
