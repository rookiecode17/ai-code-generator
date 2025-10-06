package com.raven.aicodegenerator.common;

import lombok.Data;

import java.io.Serializable;

/**
 * delete request wrapper class
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}