package com.example.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    private static final long serialVersionUId = 1L;

    private String id;

    private String username;
}
