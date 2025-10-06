package com.raven.aicodegenerator.common;

import lombok.Data;

/**
 * page request wrapper class
 */
@Data
public class PageRequest {

    /**
     * current page
     */
    private int pageNum = 1;

    /**
     * page size
     */
    private int pageSize = 10;

    /**
     * field sorting
     */
    private String sortField;

    /**
     * sorting order(descending by default)
     */
    private String sortOrder = "descend";
}