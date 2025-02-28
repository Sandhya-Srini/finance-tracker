package com.finst.financetracker.util;

import com.finst.financetracker.vocabulary.Filter;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class FilterAndSortUtil {
    private static final Pattern PATTERN = Pattern.compile("(\\w+)\\.eq\\(([^)]+)\\)");
    public List<Filter> extractFilters(List<String> filter){
        if(filter!= null) {
            return mapToFilterFields(filter);
        }
        else return Collections.emptyList();
    }

    public List<Filter> mapToFilterFields(List<String> filterCondition) {
        List<Filter> mappedFilters = new ArrayList<>();
        if(filterCondition!=null && !filterCondition.isEmpty()) {
            for (String filter : filterCondition) {
                var matcher = PATTERN.matcher(filter);
                if (matcher.matches()) {
                    var key = matcher.group(1);
                    var value = matcher.group(2);

                    mappedFilters.add(new Filter(key, value));
                }
            }
        }
        return mappedFilters;
    }

    public Pair<String,String> mapToSortField(String sort) {
        String sortField ;
        String sortOrder ;
        String mappedSortField;
        if(sort!=null) {
            if (sort.startsWith("-")) {
                sortOrder = "desc";
            }  else {
                sortOrder = "asc";
            }
            sortField = sort.substring(1);
            mappedSortField = switch (sortField) {
                case "userId" -> "userId";
                case "type" -> "type";
                case "category" -> "category";
                default -> "amount";
            };
        }else {
            sortOrder = "desc";
            mappedSortField = "amount";
        }

        return Pair.of(mappedSortField,sortOrder);
    }
}
