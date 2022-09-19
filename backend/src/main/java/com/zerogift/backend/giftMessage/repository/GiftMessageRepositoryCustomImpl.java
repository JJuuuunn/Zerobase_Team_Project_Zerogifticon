package com.zerogift.backend.giftMessage.repository;

import static com.zerogift.backend.giftBox.entity.QGiftBox.giftBox;
import static com.zerogift.backend.giftMessage.entity.QGiftMessage.giftMessage;
import static com.zerogift.backend.member.entity.QMember.member;
import static com.zerogift.backend.product.entity.QProduct.product;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerogift.backend.giftBox.entity.GiftBox;
import com.zerogift.backend.giftMessage.dto.GiftMessageDto;
import com.zerogift.backend.giftMessage.dto.GiftMessageForm;
import com.zerogift.backend.giftMessage.entity.QGiftMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GiftMessageRepositoryCustomImpl implements GiftMessageRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public GiftMessageForm findByGiftMessageForm(Long giftBoxId, Long memberId) {
        return queryFactory
            .select(Projections.constructor(GiftMessageForm.class,
                member.nickname,
                product.mainImageUrl
            ))
            .from(giftBox)
            .innerJoin(giftBox.product, product)
            .innerJoin(giftBox.sendMember, member)
            .where(giftBox.id.eq(giftBoxId)
                .and(member.id.eq(memberId)))
            .fetchOne();
    }

    @Override
    public Optional<GiftMessageDto> findByGiftMessage(Long giftMessageId, Long memberId) {
        return Optional.ofNullable(queryFactory
            .select(Projections.constructor(GiftMessageDto.class,
                product.mainImageUrl,
                member.nickname,
                giftMessage.message
            ))
            .from(giftMessage)
            .innerJoin(giftMessage.product, product)
            .innerJoin(giftMessage.fromMember, member)
            .where(giftMessage.id.eq(giftMessageId)
                .and(member.id.eq(memberId)))
            .fetchOne());
    }
}