-- NOVA: Migration chọn một nhóm sản phẩm hiện có làm nổi bật; giữ dữ liệu giá, ảnh và tồn kho, không tạo lại toàn bộ CSDL.
-- Bổ sung Nổi bật từ sản phẩm đang có; giữ nguyên giá, tồn kho, ảnh và các lựa chọn nổi bật khác.
BEGIN;
SET LOCAL lock_timeout = '5s';
SET LOCAL statement_timeout = '30s';
DO $check$
BEGIN
    IF (SELECT count(*) FROM shop.products p
        JOIN shop.categories c ON c.id=p.category_id AND c.is_active
        JOIN shop.brands b ON b.id=p.brand_id AND b.is_active
        WHERE p.code IN ('iphone-16','galaxy-s25','ipad-a16','galaxy-tab-s10-fe-5g','macbook-air-m4-13','macbook-air-m4-15','galaxy-buds3-pro','sony-wh-1000xm5','watch-s10-42-gps','watch-s10-46-lte','pad7-keyboard-touchpad','apple-charger-20w') AND p.status='active'
          AND EXISTS (SELECT 1 FROM shop.product_variants v WHERE v.product_id=p.id AND v.status='active')
          AND EXISTS (SELECT 1 FROM shop.product_images i WHERE i.product_id=p.id AND nullif(btrim(i.url),'') IS NOT NULL)) <> 12 THEN
        RAISE EXCEPTION 'Danh sach 12 san pham noi bat chua san sang; chua cap nhat.';
    END IF;
END $check$;
WITH chosen(code) AS (VALUES
    ('iphone-16'),
    ('galaxy-s25'),
    ('ipad-a16'),
    ('galaxy-tab-s10-fe-5g'),
    ('macbook-air-m4-13'),
    ('macbook-air-m4-15'),
    ('galaxy-buds3-pro'),
    ('sony-wh-1000xm5'),
    ('watch-s10-42-gps'),
    ('watch-s10-46-lte'),
    ('pad7-keyboard-touchpad'),
    ('apple-charger-20w')
)
UPDATE shop.products p SET is_featured=true
FROM chosen WHERE p.code=chosen.code AND p.status='active' AND NOT p.is_featured;
COMMIT;
