package com.younghu.android.socialtruth.mapper;

// Credit to https://github.com/gonzalonm/viewmodel-firebase-sample/
// blob/master/data/src/main/java/com/gonzalonm/viewmodelfirebase/data/mapper/IMapper.java
public interface IMapper<From, To> {

    To map(From from);
}
