# DB 성능 개선 보고서

## 조회 기능 리스트
1. 유저 식별자로 조회
2. 상품 리스트 조회
3. 상품 식별자로 단건 조회
4. 상품 식별자 리스트로 리스트 조회
5. 쿠폰 아이템 식별자로 조회
6. 유저 보유 쿠폰 조회
7. 쿠폰 식별자로 조회
8. 기간에 해당하는 주문 내역 조회
9. 기간에 해당하는 주문 통계 조회

위 리스트에서 PK가 아닌 값으로 조회하는 쿼리들이 데이터가 많아지면 병목 쿼리가 될 수 있는 가능성이 높다고 생각하여 아래 케이스들의 인덱스 전과 후를 비교하고자 합니다.
```text
6. 유저 보유 쿠폰 조회
8. 기간에 해당하는 주문 내역 조회
9. 기간에 해당하는 주문 통계 조회
```

## 유저 보유 쿠폰 조회
유저 보유 쿠폰을 조회하기 위해서는 coupon_item에서 user_id를 조건으로 조회 쿼리를 사용하게 됩니다. </br>
2개의 테스트 케이스에서 coupon_item에 user_id에 인덱스 유무에 따른 차이를 비교합니다. <br>
1. users에는 10000건, coupon_item에 user당 100건 (총 1_000_000건)
2. users에는 10000건, coupon_item에 10명의 user가 100_000건(총 1_000_000건)

### 1. 테스트 쿼리
```sql
SELECT * 
FROM coupon_item as ci
	inner join users as u
    on ci.user_id = u.id
WHERE ci.user_id = 23;
```
### 2.1. 인덱스 적용 전 실행 계획
```sql
-> Filter: (ci.user_id = 23)  
  (cost=98513 rows=97527)  
  (actual time=1.73..232 rows=100 loops=1)  
    -> Table scan on ci  
      (cost=98513 rows=975274)  
      (actual time=1.12..194 rows=1e+6 loops=1)
```
### 2.2. 인덱스 적용 후 실행 계획
```sql
-> Index lookup on ci using idx_user_id (user_id=23)  
  (cost=35 rows=100)  
  (actual time=0.162..0.182 rows=100 loops=1)
```

### 3. 성능 분석
| 지표            | 인덱스 적용 전   | 인덱스 적용 후 |
|---------------|------------|----------|
| **실행 시간**     | 232 ms     | 0.182 ms |    
| **비용**        | 98,513     | 35       |    
| **데이터 검색 방식** | 전체 테이블 스캔  | 인덱스 검색   |
| **처리 행 수**    | 1,000,000건 | 100건     |

### 2번 coupon_item에 10명의 유저가 모두 가진 경우 
2번 케이스는 user_id가 1~10인 유저가 100,000건씩 모든 쿠폰 아이템을 가진 경우입니다. <br>
### 1.1. 테스트 쿼리 1 (100,000건)
```sql
SELECT * 
FROM coupon_item as ci
	inner join users as u
    on ci.user_id = u.id
WHERE ci.user_id = 4;
```
### 1.2. 테스트 쿼리 2 (0건)
```sql
SELECT * 
FROM coupon_item as ci
	inner join users as u
    on ci.user_id = u.id
WHERE ci.user_id = 23;
```
### 2.1 인덱스 적용 전 실행 계획 (`user_id=4` 쿼리)
```sql
-> Filter: (ci.user_id = 4)
(cost=98312 rows=97326)
(actual time=96..237ms rows=100,000 loops=1)
-> Table scan on ci
(cost=98312 rows=973,261)
(actual time=1.46..196ms rows=1,000,000 loops=1)
```
### 2.2 인덱스 적용 후 실행 계획 (`user_id=4` 쿼리)
```sql
-> Index lookup on ci using idx_user_id (user_id=4)
(cost=23,626 rows=206,690)
(actual time=5.43..81.7ms rows=100,000 loops=1)
```
### 2.3 인덱스 적용 후 실행 계획 (`user_id=23` 쿼리)
```sql
-> Index lookup on ci using idx_user_id (user_id=23)
(cost=0.35 rows=1)
(actual time=0.0513ms rows=0 loops=1)
```
### 3. 성능 분석

| 지표              | 인덱스 적용 전 (user_id=4) | 인덱스 적용 후 (user_id=4) | 인덱스 적용 후 (user_id=23) |
|-------------------|----------------------|----------------------|-----------------------|
| **실행 시간**     | 237ms                | 81.7ms               | 0.051ms               |
| **비용**      | 98,312               | 23,626 (-76%)        | 0.35                  |
| **데이터 검색 방식**     | 전체 테이블 스캔            | 인덱스 검색               | 인덱스 검색                |
| **처리 행 수**    | 1,000,000건           | 100,000건             | 0건                    |

### 결론
user_id에 인덱스를 추가하였을 때 균등하게 분포된 경우와 특정 유저에게 집중된 경우 모두 조회 성능이 개선되었으므로 적절한 인덱스라고 생각하게 되었습니다.
- **균등한 경우**: 232ms → 0.182ms
- **집중된 경우**: 237ms → 81.7ms

## 기간에 해당하는 주문 내역 조회
인기 판매 상품 조회를 위해서 기간에 해당하는 주문 내역을 조회하고 있습니다.
Order에 orderAt에 인덱스 유무에 따른 차이를 비교합니다.

### 1. 테스트 쿼리
```sql
SELECT *
	FROM order_item AS oi
		INNER JOIN orders AS o
        ON oi.order_id = o.id
	WHERE o.order_at BETWEEN '2025-04-17' AND '2025-04-18';
```
### 2.1. 인덱스 적용 전 실행 계획
```sql
-> Nested loop inner join
    -> Filter: (o.order_at between ...)  -- 풀스캔 후 필터링
        -> Table scan on o  -- 10,000건 전체 스캔
    -> Index lookup on oi  -- order_id 인덱스 활용
```

### 2.2. 인덱스 적용 후 실행 계획
```sql
-> Nested loop inner join
    -> Index range scan on o using idx_order_at  -- 인덱스 범위 검색
    -> Index lookup on oi  -- order_id 인덱스 활용
```

### 3. 성능 분석
| 지표               | 인덱스 적용 전            | 인덱스 적용 후         |
|--------------------|---------------------|------------------|
| **총 실행 시간**   | 9.89ms              | 1.27ms           |
| **Orders 접근 방식** | 전체 테이블 스캔 (10,000건) | 인덱스 범위 검색 (16건)  |
| **예상 비용**      | 3,072               | 37.6             |
| **실제 I/O 부하**  | 4.64ms (전체 스캔)      | 0.213ms (인덱스 검색) |


### 4. 결론
order_at에 인덱스를 적용 했을 때 인덱스 범위 검색으로 조회 성능이 개선되어 적절한 인덱스라고 판단됩니다.

## 기간에 해당하는 주문 통계 조회
기간에 해당하는 판매량 상위 5개의 productId를 조회하는 쿼리에 인덱스 유무에 따른 차이를 비교합니다.
```sql
/** 적용 인덱스 **/
ALTER TABLE order_statistics
  ADD INDEX idx_stat_date_product_sold (statistic_date, product_id, sold_quantity);
```

### 1. 테스트 쿼리
```sql
SELECT
    product_id
FROM order_statistics os
WHERE statistic_date BETWEEN '2025-04-01' AND '2025-04-04'
GROUP BY product_id
ORDER BY SUM(sold_quantity) DESC
    LIMIT 5;
```
### 2.1 인덱스 적용 전
```sql
-> Limit: 5 row(s)  (actual time=32.1..32.1 rows=5 loops=1)
    -> Sort: `sum(os.sold_quantity)` DESC, limit input to 5 row(s) per chunk  (actual time=32.1..32.1 rows=5 loops=1)
        -> Table scan on <temporary>  (actual time=31.9..32 rows=499 loops=1)
            -> Aggregate using temporary table  (actual time=31.9..31.9 rows=499 loops=1)
                -> Filter: (os.statistic_date between '2025-04-01' and '2025-04-03')  (cost=4951 rows=5456) (actual time=1.28..29.7 rows=4973 loops=1)
                    -> Table scan on os  (cost=4951 rows=49107) (actual time=1.26..14.6 rows=50000 loops=1)
```

### 2.2 커버링 인덱스 적용 후
```sql
-> Limit: 5 row(s)  (actual time=6.73..6.73 rows=5 loops=1)
    -> Sort: `sum(os.sold_quantity)` DESC, limit input to 5 row(s) per chunk  (actual time=6.68..6.68 rows=5 loops=1)
        -> Table scan on <temporary>  (actual time=5.95..6.01 rows=499 loops=1)
            -> Aggregate using temporary table  (actual time=5.67..5.67 rows=499 loops=1)
                -> Filter: (os.statistic_date between '2025-04-01' and '2025-04-03')  (cost=1009 rows=4973) (actual time=0.312..3.64 rows=4973 loops=1)
                    -> Covering index range scan on os using idx_stat_date_product_sold over ('2025-04-01' <= statistic_date <= '2025-04-03')  (cost=1009 rows=4973) (actual time=0.3..1.81 rows=4973 loops=1)
```
### 3. 성능 분석
| 구분               | 인덱스 없음 | 인덱스 사용 |
|--------------------|-------------|-------------|
| **필터링 시간**    | 29.7ms      | 3.64ms      |
| **총 실행 시간**   | 32.1ms      | 6.73ms      |
| **접근 데이터량**  | 50,000건    | 4,973건     |

### 4. 결론
커버링 인덱스를 적용하여 필터링 시간 및 총 실행 시간이 감소하고, 접근 데이터량도 크게 줄어드는 것을 볼 수 있습니다.
