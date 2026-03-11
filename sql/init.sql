-- 非遗传承管理系统 数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS feiyi DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE feiyi;

-- ============================================
-- 1. 用户表
-- ============================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色: ADMIN/USER/INHERITOR',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '用户表';

-- ============================================
-- 2. 非遗项目分类表
-- ============================================
DROP TABLE IF EXISTS heritage_category;
CREATE TABLE heritage_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    icon VARCHAR(255) COMMENT '分类图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '非遗项目分类表';

-- ============================================
-- 3. 非遗项目表
-- ============================================
DROP TABLE IF EXISTS heritage_project;
CREATE TABLE heritage_project (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL COMMENT '项目名称',
    category_id BIGINT COMMENT '分类ID',
    level VARCHAR(20) COMMENT '级别: 国家级/省级/市级/县级',
    region VARCHAR(100) COMMENT '所属地区',
    description TEXT COMMENT '项目简介',
    content TEXT COMMENT '详细内容',
    cover_url VARCHAR(255) COMMENT '封面图片',
    images VARCHAR(1000) COMMENT '图片集(JSON数组)',
    video_url VARCHAR(255) COMMENT '视频链接',
    longitude DECIMAL(10,6) COMMENT '经度',
    latitude DECIMAL(10,6) COMMENT '纬度',
    status TINYINT DEFAULT 1 COMMENT '0下架 1上架',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '非遗项目表';

-- ============================================
-- 4. 传承人表
-- ============================================
DROP TABLE IF EXISTS inheritor;
CREATE TABLE inheritor (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    avatar VARCHAR(255) COMMENT '头像',
    gender VARCHAR(4) COMMENT '性别',
    birth_year INT COMMENT '出生年份',
    level VARCHAR(20) COMMENT '级别',
    project_id BIGINT COMMENT '关联非遗项目ID',
    region VARCHAR(100) COMMENT '所在地区',
    bio TEXT COMMENT '个人简介',
    story TEXT COMMENT '传承故事',
    skills VARCHAR(500) COMMENT '擅长技艺',
    honors VARCHAR(500) COMMENT '荣誉成就',
    images VARCHAR(1000) COMMENT '图片集(JSON数组)',
    video_url VARCHAR(255) COMMENT '视频链接',
    longitude DECIMAL(10,6) COMMENT '经度',
    latitude DECIMAL(10,6) COMMENT '纬度',
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '传承人表';

-- ============================================
-- 5. 活动资讯表
-- ============================================
DROP TABLE IF EXISTS activity;
CREATE TABLE activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL COMMENT '活动标题',
    cover_url VARCHAR(255) COMMENT '封面图',
    images VARCHAR(1000) COMMENT '图片集(JSON数组)',
    video_url VARCHAR(255) COMMENT '视频链接',
    content TEXT COMMENT '活动内容',
    location VARCHAR(200) COMMENT '活动地点',
    longitude DECIMAL(10,6) COMMENT '经度',
    latitude DECIMAL(10,6) COMMENT '纬度',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    max_participants INT DEFAULT 0 COMMENT '最大参与人数, 0为不限',
    current_participants INT DEFAULT 0 COMMENT '当前报名人数',
    status TINYINT DEFAULT 1 COMMENT '0草稿 1发布 2进行中 3已结束',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '活动资讯表';

-- ============================================
-- 6. 活动预约表
-- ============================================
DROP TABLE IF EXISTS activity_reservation;
CREATE TABLE activity_reservation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT DEFAULT 0 COMMENT '0待审核 1已通过 2已拒绝 3已取消',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '活动预约表';

-- ============================================
-- 7. 商品分类表
-- ============================================
DROP TABLE IF EXISTS product_category;
CREATE TABLE product_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    icon VARCHAR(255) COMMENT '图标',
    sort_order INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '商品分类表';

-- ============================================
-- 8. 商品表
-- ============================================
DROP TABLE IF EXISTS product;
CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    category_id BIGINT COMMENT '分类ID',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    cover_url VARCHAR(255) COMMENT '封面图',
    images VARCHAR(1000) COMMENT '图片集(JSON)',
    description TEXT COMMENT '商品描述',
    detail TEXT COMMENT '商品详情',
    stock INT DEFAULT 0 COMMENT '库存',
    sales INT DEFAULT 0 COMMENT '销量',
    status TINYINT DEFAULT 1 COMMENT '0下架 1上架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '商品表';

-- ============================================
-- 9. 订单表
-- ============================================
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status TINYINT DEFAULT 0 COMMENT '0待付款 1已付款 2已发货 3已完成 4已取消',
    address_info VARCHAR(500) COMMENT '收货地址JSON',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '订单表';

-- ============================================
-- 10. 订单明细表
-- ============================================
DROP TABLE IF EXISTS order_item;
CREATE TABLE order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(100),
    product_cover VARCHAR(255),
    price DECIMAL(10,2),
    quantity INT DEFAULT 1,
    subtotal DECIMAL(10,2)
) COMMENT '订单明细表';

-- ============================================
-- 11. 收货地址表
-- ============================================
DROP TABLE IF EXISTS user_address;
CREATE TABLE user_address (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    receiver VARCHAR(50) COMMENT '收货人',
    phone VARCHAR(20) COMMENT '手机号',
    province VARCHAR(50),
    city VARCHAR(50),
    district VARCHAR(50),
    detail VARCHAR(200) COMMENT '详细地址',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '收货地址表';

-- ============================================
-- 12. 留言板表
-- ============================================
DROP TABLE IF EXISTS message_board;
CREATE TABLE message_board (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '用户ID, NULL为匿名',
    nickname VARCHAR(50) COMMENT '昵称',
    content TEXT NOT NULL COMMENT '留言内容',
    reply TEXT COMMENT '管理员回复',
    status TINYINT DEFAULT 0 COMMENT '0待审核 1已通过 2已拒绝',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '留言板表';

-- ============================================
-- 13. 轮播图表
-- ============================================
DROP TABLE IF EXISTS banner;
CREATE TABLE banner (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) COMMENT '标题',
    image_url VARCHAR(255) NOT NULL COMMENT '图片地址',
    link_url VARCHAR(255) COMMENT '跳转链接',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '0禁用 1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '轮播图表';

-- ============================================
-- 14. 操作日志表
-- ============================================
DROP TABLE IF EXISTS operation_log;
CREATE TABLE operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '操作人ID',
    username VARCHAR(50) COMMENT '操作人',
    operation VARCHAR(100) COMMENT '操作描述',
    method VARCHAR(200) COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    ip VARCHAR(50) COMMENT 'IP地址',
    duration BIGINT COMMENT '耗时(ms)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '操作日志表';

-- ============================================
-- 初始化数据
-- ============================================

-- 用户数据由应用启动时DataInitializer自动初始化(BCrypt加密)
-- 默认账户: admin/admin123(管理员) inheritor1/admin123(传承人) user1/admin123(普通用户)

-- 非遗分类
INSERT INTO heritage_category (name, sort_order) VALUES
('传统技艺', 1),
('传统美术', 2),
('传统音乐', 3),
('传统舞蹈', 4),
('传统戏剧', 5),
('民间文学', 6),
('传统医药', 7),
('民俗', 8);

-- 非遗项目
INSERT INTO heritage_project (title, category_id, level, region, description, content, cover_url, video_url, longitude, latitude, status, view_count) VALUES
('景德镇手工制瓷技艺', 1, '国家级', '江西省景德镇市',
 '景德镇手工制瓷技艺是中国陶瓷文化的杰出代表，拥有千年历史传承。',
 '景德镇手工制瓷技艺历史悠久，始于汉代，兴于唐宋，盛于明清。其制作工序包括拉坯、利坯、画坯、施釉、烧窑等七十二道工序，每一道都凝聚着匠人的心血与智慧。景德镇瓷器以"白如玉、明如镜、薄如纸、声如磬"著称于世，是中华文明的重要名片。2006年被列入第一批国家级非物质文化遗产名录。',
 'https://images.unsplash.com/photo-1565193566173-7a0ee3dbe261?w=800&h=600&fit=crop', NULL,
 117.178419, 29.268835, 1, 1256),

('苏绣', 2, '国家级', '江苏省苏州市',
 '苏绣是中国四大名绣之一，以精细雅洁著称，被誉为"东方艺术明珠"。',
 '苏绣起源于苏州，有着2000多年的历史。苏绣技法以"精、细、雅、洁"著称，绣工细腻、针法灵活、色彩清雅。代表作品有双面绣、乱针绣等，题材广泛，包括人物、山水、花鸟等。苏绣不仅是一种工艺品，更承载着江南水乡的文化底蕴和审美意趣。',
 'https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=800&h=600&fit=crop', NULL,
 120.619585, 31.299379, 1, 980),

('昆曲', 5, '国家级', '江苏省苏州市',
 '昆曲是中国最古老的剧种之一，被联合国教科文组织列为"人类口头和非物质遗产代表作"。',
 '昆曲发源于14世纪的昆山地区，至今已有600多年历史。昆曲以其优美典雅的唱腔、细腻婉转的表演、精美绝伦的服饰道具闻名于世。代表剧目有《牡丹亭》《长生殿》《桃花扇》等。2001年被联合国教科文组织列为首批"人类口头和非物质遗产代表作"。',
 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=800&h=600&fit=crop', NULL,
 120.980736, 31.385598, 1, 876),

('中医针灸', 7, '国家级', '全国',
 '中医针灸是中华传统医学的瑰宝，已被列入联合国教科文组织人类非物质文化遗产代表作名录。',
 '中医针灸起源于中国，有着数千年历史。针灸通过刺激人体穴位来调节气血运行、疏通经络，达到防治疾病的目的。针灸理论体系博大精深，包含经络学说、腧穴学说等核心内容。2010年被列入联合国教科文组织"人类非物质文化遗产代表作名录"。',
 'https://images.unsplash.com/photo-1512290923902-8a9f81dc236c?w=800&h=600&fit=crop', NULL,
 116.407395, 39.904211, 1, 1102),

('京剧', 5, '国家级', '北京市',
 '京剧是中国五大戏曲剧种之一，被视为中国国粹，以其独特的艺术魅力享誉世界。',
 '京剧形成于清代乾隆年间，融合了徽剧、汉剧等多种戏曲艺术精华。京剧以唱、念、做、打为基本表演手段，行当分为生、旦、净、丑四大类。脸谱艺术是京剧最具代表性的特色之一，不同颜色和图案代表不同的人物性格。2010年被列入联合国教科文组织"人类非物质文化遗产代表作名录"。',
 'https://images.unsplash.com/photo-1513171920199-abf91c0b2f18?w=800&h=600&fit=crop', NULL,
 116.407395, 39.904211, 1, 2340),

('剪纸', 2, '国家级', '全国',
 '中国剪纸是一种用剪刀或刻刀在纸上剪刻花纹的民间艺术，有着1500多年历史。',
 '剪纸艺术在中国各地广泛流传，风格各异。北方剪纸粗犷豪放，南方剪纸精巧秀丽。剪纸题材丰富，包括花鸟鱼虫、人物故事、吉祥图案等，常用于节日装饰、婚庆喜事等场合。2009年被列入联合国教科文组织"人类非物质文化遗产代表作名录"。',
 'https://images.unsplash.com/photo-1582738411706-bfc8e691d1c2?w=800&h=600&fit=crop', NULL,
 108.948024, 34.263161, 1, 1567),

('古琴艺术', 3, '国家级', '全国',
 '古琴是中国最古老的弹拨乐器之一，有着3000多年历史，位列"琴棋书画"之首。',
 '古琴又称瑶琴、玉琴，是中国传统文化中地位崇高的乐器。古琴音色深沉悠远，其演奏讲究"意在弦外"的美学境界。历代文人雅士将古琴视为修身养性的重要工具。代表曲目有《高山流水》《广陵散》《平沙落雁》等。2003年被联合国教科文组织列为"人类口头和非物质遗产代表作"。',
 'https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=800&h=600&fit=crop', NULL,
 116.407395, 39.904211, 1, 789),

('皮影戏', 5, '国家级', '陕西省',
 '皮影戏是中国民间古老的传统艺术，又称"影子戏"或"灯影戏"，始于西汉。',
 '皮影戏以兽皮或纸板做成的人物剪影来表演故事的民间戏剧形式。艺人在白色幕布后面操纵皮影人物，配以音乐和唱腔，表演各种故事。皮影的制作工艺精湛，需经过选皮、制皮、画稿、过稿、镂刻、敷彩、发汗、缀结等八道工序。2011年被列入联合国教科文组织"人类非物质文化遗产代表作名录"。',
 'https://images.unsplash.com/photo-1547036967-23d11aacaee0?w=800&h=600&fit=crop', NULL,
 108.948024, 34.263161, 1, 934);

-- 传承人
INSERT INTO inheritor (name, avatar, gender, birth_year, level, project_id, region, bio, skills, honors) VALUES
('王文德', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&h=200&fit=crop', '男', 1952, '国家级', 1, '江西省景德镇市',
 '王文德大师从事手工制瓷五十余年，是景德镇传统制瓷技艺的杰出代表。自幼随父学艺，精通拉坯、利坯等核心工序，作品多次在国内外展览中获奖。',
 '手工拉坯、青花绘制、釉下彩装饰', '中国工艺美术大师、国家级非遗传承人'),

('陈慧娟', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=200&h=200&fit=crop', '女', 1965, '国家级', 2, '江苏省苏州市',
 '陈慧娟大师是苏绣艺术的当代领军人物，从艺四十年来创作了大量精品力作。她在传统技法基础上大胆创新，将现代绘画元素融入苏绣创作。',
 '双面绣、乱针绣、细绣', '江苏省工艺美术大师、国家级非遗传承人'),

('张继青', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=200&h=200&fit=crop', '女', 1958, '国家级', 3, '江苏省苏州市',
 '张继青老师是当代昆曲表演艺术家，师从多位昆曲名家，功底深厚。她的表演细腻入微，唱腔优美动人，为昆曲艺术的传承做出了重要贡献。',
 '昆曲旦角表演、昆曲唱腔', '中国戏剧梅花奖获得者、国家级非遗传承人'),

('李铁锤', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200&h=200&fit=crop', '男', 1970, '省级', 6, '陕西省西安市',
 '李铁锤老师是陕西剪纸艺术的代表性传承人，其作品风格粗犷豪放，富有浓郁的黄土高原气息。多年来致力于剪纸艺术的教学和传播工作。',
 '剪纸创作、窗花设计、剪纸教学', '陕西省工艺美术大师、省级非遗传承人'),

('赵明远', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=200&h=200&fit=crop', '男', 1948, '国家级', 7, '北京市',
 '赵明远先生是古琴艺术的重要传承人，师从广陵派名家，琴艺精湛。他的演奏风格古朴典雅，深得古琴艺术神韵，长期致力于古琴文化的研究与推广。',
 '古琴演奏、琴曲创作、古琴制作', '国家级非遗传承人、中国古琴学会理事');

-- 活动
INSERT INTO activity (title, cover_url, content, location, longitude, latitude, start_time, end_time, max_participants, current_participants, status) VALUES
('非遗进校园——剪纸艺术体验课', 'https://images.unsplash.com/photo-1588072432836-e10032774350?w=800&h=400&fit=crop',
 '邀请国家级剪纸传承人走进校园，为同学们带来精彩的剪纸艺术体验课。课程将介绍剪纸的历史渊源、基本技法，并指导同学们亲手创作剪纸作品。通过这次活动，希望能让更多年轻人感受到传统手工艺的魅力。',
 '北京市朝阳区文化馆', 116.443108, 39.921147, '2026-04-15 09:00:00', '2026-04-15 17:00:00', 50, 32, 1),

('景德镇陶瓷文化周', 'https://images.unsplash.com/photo-1565193566173-7a0ee3dbe261?w=800&h=400&fit=crop',
 '为期一周的景德镇陶瓷文化体验活动，包含手工拉坯体验、青花瓷绘画、窑炉参观等丰富内容。由多位制瓷大师亲自指导，让参与者深入了解千年瓷都的文化魅力。',
 '江西省景德镇市古窑民俗博览区', 117.178419, 29.268835, '2026-05-01 08:30:00', '2026-05-07 17:00:00', 100, 67, 1),

('昆曲雅集——牡丹亭专场演出', 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=800&h=400&fit=crop',
 '特邀国家级昆曲传承人张继青老师领衔主演经典剧目《牡丹亭》。演出前设有昆曲艺术讲座，帮助观众了解昆曲的历史与美学特征。演出结束后设有演员见面会环节。',
 '苏州市昆曲博物馆', 120.619585, 31.299379, '2026-04-20 19:00:00', '2026-04-20 21:30:00', 200, 156, 1),

('古琴文化沙龙', 'https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=800&h=400&fit=crop',
 '以古琴为媒介，开展一场雅致的文化沙龙。活动包含古琴演奏欣赏、琴学知识讲座、古琴体验等环节，让参与者在悠扬的琴声中感受中华传统文化的深邃与优雅。',
 '北京市西城区国家图书馆古籍馆', 116.359457, 39.941037, '2026-05-10 14:00:00', '2026-05-10 17:00:00', 30, 28, 1),

('端午节非遗市集', 'https://images.unsplash.com/photo-1533900298318-6b8da08a523e?w=800&h=400&fit=crop',
 '端午佳节之际，汇集全国各地非遗手工艺人，举办大型非遗文化市集。现场设有传统手工艺展示、非遗美食品鉴、民俗文化体验等多个区域，市民可现场观摩技艺、参与互动、选购文创产品。',
 '南京市夫子庙景区', 118.787095, 32.016570, '2026-05-31 09:00:00', '2026-06-02 18:00:00', 0, 0, 1);

-- 商品分类
INSERT INTO product_category (name, sort_order) VALUES
('陶瓷器皿', 1),
('刺绣织品', 2),
('剪纸艺术', 3),
('文房四宝', 4),
('茶器茶具', 5),
('首饰配饰', 6);

-- 商品
INSERT INTO product (name, category_id, price, original_price, cover_url, description, detail, stock, sales, status) VALUES
('景德镇手绘青花瓷茶杯', 1, 168.00, 238.00,
 'https://images.unsplash.com/photo-1578749556568-bc2c40e68b61?w=600&h=600&fit=crop',
 '纯手工绘制青花纹饰，传统柴窑烧制',
 '选用优质高岭土，经72道传统工序精心制作。杯身绘有经典青花纹饰，笔法流畅、意境深远。釉面温润如玉，手感细腻。适合品茗、收藏、馈赠。', 200, 156, 1),

('苏绣双面绣台屏——牡丹图', 2, 1280.00, 1680.00,
 'https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=600&h=600&fit=crop',
 '苏绣大师手工制作，双面异色异形绣',
 '由苏绣传承人精心创作的双面绣台屏，正面为盛开的牡丹花，背面为翩翩蝴蝶，双面图案各异、色彩不同，展现了苏绣技艺的精湛水平。配以红木框架，典雅大方。', 30, 18, 1),

('手工剪纸——十二生肖套装', 3, 88.00, 128.00,
 'https://images.unsplash.com/photo-1582738411706-bfc8e691d1c2?w=600&h=600&fit=crop',
 '传承人手工剪制，含精美礼盒包装',
 '由省级剪纸传承人纯手工剪制的十二生肖剪纸套装。每幅作品造型生动、线条流畅，充分展现了中国剪纸艺术的独特魅力。附赠精美礼盒和剪纸艺术说明书。', 500, 342, 1),

('端砚——老坑麻子石', 4, 2680.00, 3200.00,
 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=600&fit=crop',
 '广东肇庆老坑端砚，文人墨客必备',
 '选用广东肇庆老坑天然麻子石精心雕琢而成。石质细腻、发墨均匀、贮水不涸。砚面雕刻山水云纹，刀法精湛、意境悠远。附赠红木砚盒，可观可用可藏。', 15, 8, 1),

('龙泉青瓷茶具套装', 5, 580.00, 780.00,
 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600&h=600&fit=crop',
 '龙泉青瓷传统烧制工艺，梅子青釉色',
 '传承龙泉青瓷千年烧制工艺，釉色青翠欲滴。套装包含茶壶一把、茶杯六只、公道杯一只、茶漏一只。釉面温润细腻，线条简约大方，是品茗待客的上选之器。', 80, 45, 1),

('银丝花丝镶嵌胸针', 6, 368.00, 468.00,
 'https://images.unsplash.com/photo-1515562141589-67f0d569b6c6?w=600&h=600&fit=crop',
 '花丝镶嵌非遗工艺，纯银手工打造',
 '采用花丝镶嵌传统工艺，以纯银丝为原料，经掐、填、攒、焊等多道工序手工制作而成。造型为传统吉祥纹样，精致典雅，既可日常佩戴，亦具收藏价值。', 120, 67, 1),

('蜀锦织造围巾', 2, 458.00, 598.00,
 'https://images.unsplash.com/photo-1601924638867-3a6de6b7a500?w=600&h=600&fit=crop',
 '四川蜀锦传统织造工艺，真丝材质',
 '采用蜀锦传统提花织造工艺，以优质桑蚕丝为原料织制。色彩绚丽、图案精美，呈现出蜀锦独特的华贵气质。质地柔软亲肤，四季皆宜佩戴。', 60, 33, 1),

('紫砂壶——仿古石瓢', 5, 880.00, 1080.00,
 'https://images.unsplash.com/photo-1530968033775-2c92736b131e?w=600&h=600&fit=crop',
 '宜兴紫砂传统手工成型，原矿紫泥',
 '选用宜兴黄龙山原矿紫泥，由紫砂工艺师全手工拍打成型。壶型为经典石瓢造型，线条流畅、比例协调。经高温烧制，透气性好，泡茶香醇。附赠作者证书。', 40, 22, 1);

-- 轮播图
INSERT INTO banner (title, image_url, link_url, sort_order, status) VALUES
('探索非遗之美', 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=1200&h=500&fit=crop', '/heritage', 1, 1),
('传承匠心精神', 'https://images.unsplash.com/photo-1565193566173-7a0ee3dbe261?w=1200&h=500&fit=crop', '/inheritors', 2, 1),
('非遗文创好物', 'https://images.unsplash.com/photo-1578749556568-bc2c40e68b61?w=1200&h=500&fit=crop', '/shop', 3, 1),
('精彩活动预约', 'https://images.unsplash.com/photo-1533900298318-6b8da08a523e?w=1200&h=500&fit=crop', '/activities', 4, 1);

-- 留言板
INSERT INTO message_board (user_id, nickname, content, reply, status, create_time) VALUES
(3, '文化爱好者', '这个平台做得非常好，让我对非遗文化有了更深入的了解，希望能多举办一些线下体验活动！', '感谢您的支持与建议，我们会持续推出更多精彩活动，敬请关注！', 1, '2026-02-15 10:30:00'),
(NULL, '匿名用户', '第一次接触剪纸艺术就被深深吸引了，想问一下有没有线上教学课程？', '您好！我们正在筹备线上教学模块，预计下个季度上线，届时会有多位传承人录制教学视频。', 1, '2026-02-20 14:20:00'),
(NULL, '匿名用户', '在商城买的青花瓷茶杯质量很好，包装也很精美，非常满意！', NULL, 1, '2026-03-01 09:15:00'),
(3, '文化爱好者', '建议增加一个非遗地图功能，可以按地区查看各地的非遗项目分布。', '感谢宝贵建议！地图功能已在规划中，会尽快上线。', 1, '2026-03-05 16:45:00');
