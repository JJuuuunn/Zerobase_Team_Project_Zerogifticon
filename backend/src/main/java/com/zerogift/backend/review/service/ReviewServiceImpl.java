package com.zerogift.backend.review.service;

import com.zerogift.backend.common.dto.Result;
import com.zerogift.backend.common.exception.code.MemberErrorCode;
import com.zerogift.backend.common.exception.code.ProductErrorCode;
import com.zerogift.backend.common.exception.code.ReviewErrorCode;
import com.zerogift.backend.common.exception.gift.NotFoundGiftBoxException;
import com.zerogift.backend.common.exception.member.MemberException;
import com.zerogift.backend.common.exception.product.ProductException;
import com.zerogift.backend.common.exception.review.ReviewException;
import com.zerogift.backend.giftBox.entity.GiftBox;
import com.zerogift.backend.giftBox.repository.GiftBoxRepository;
import com.zerogift.backend.member.entity.Member;
import com.zerogift.backend.member.repository.MemberRepository;
import com.zerogift.backend.product.entity.Product;
import com.zerogift.backend.product.repository.ProductRepository;
import com.zerogift.backend.review.entity.Review;
import com.zerogift.backend.review.model.ReviewInput;
import com.zerogift.backend.review.model.ReviewResponse;
import com.zerogift.backend.review.repository.ReviewRepository;
import com.zerogift.backend.security.dto.LoginInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService{

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final GiftBoxRepository giftBoxRepository;

    @Override
    public ReviewResponse addReview(LoginInfo loginInfo, Long productId, ReviewInput reviewInput) {
        // 회원 정보 가져오기
        Member member = memberRepository.findByEmail(loginInfo.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 상품 정보 가져오기
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        GiftBox giftBox = giftBoxRepository.findByRecipientMemberAndProduct(member,product)
                .orElseThrow(() -> new NotFoundGiftBoxException("존재하지 않는 선물 입니다."));

        if (!giftBox.getIsUse()) {
            throw new ReviewException(ReviewErrorCode.ADD_REVIEW_AFTER_USE);
        }

        // 리뷰 내용 저장
        Review review = Review.builder()
                .rank(reviewInput.getRank())
                .description(reviewInput.getDescription())
                .member(member)
                .product(product)
                .createDate(LocalDateTime.now())
                .build();
        reviewRepository.save(review);

        // member 와 product 내용 편집해서 출력
        return ReviewResponse.of(review);
    }

    @Transactional
    @Override
    public ReviewResponse modifyReview(LoginInfo loginInfo, Long reviewId, ReviewInput reviewInput) {
        // 회원 정보 가져오기
        Member member = memberRepository.findByEmail(loginInfo.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 수정할 리뷰 가져오기
        Review review = reviewRepository.findByMemberAndId(member, reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        // 리뷰 별점과 내용 수정
        review.modify(reviewInput.getRank(), reviewInput.getDescription());

        return ReviewResponse.of(review);
    }

    @Override
    public void removeReview(LoginInfo loginInfo, Long reviewId) {
        // 회원 정보 가져오기
        Member member = memberRepository.findByEmail(loginInfo.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 수정할 리뷰 가져오기
        Review review = reviewRepository.findByMemberAndId(member, reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        // 해당 리뷰 삭제
        reviewRepository.delete(review);
    }

    @Override
    public List<ReviewResponse> userReviewList(LoginInfo loginInfo) {
        // 회원 정보 가져오기
        Member member = memberRepository.findByEmail(loginInfo.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // review 에 들어가있는 member 와 product 정보 편집
        List<ReviewResponse> reviewResponseList = reviewRepository.findByMember(member).stream().map(
                x -> ReviewResponse.of(x)).collect(Collectors.toList());
        return reviewResponseList;
    }

    @Override
    public List<ReviewResponse> productReviewList(Long productId) {
        // 상품 정보 가져오기
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        List<Review> reviewList = reviewRepository.findByProduct(product);

        // review 에 들어가있는 member 와 product 정보 편집
        List<ReviewResponse> reviewResponseList = reviewRepository.findByProduct(product).stream().map(
                x -> ReviewResponse.of(x)).collect(Collectors.toList());
        return reviewResponseList;
    }
}