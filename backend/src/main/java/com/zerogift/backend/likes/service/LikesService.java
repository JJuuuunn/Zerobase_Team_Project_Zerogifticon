package com.zerogift.backend.likes.service;

import com.zerogift.backend.common.dto.Result;
import com.zerogift.backend.likes.model.LikesModel;
import com.zerogift.backend.security.dto.LoginInfo;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LikesService {
    // 좋아요 누르기
    ResponseEntity<Result<?>> pressLike(LoginInfo loginInfo, Long productId);

    // 좋아요 누른 리스트
    List<LikesModel> likeList(LoginInfo loginInfo);
}
