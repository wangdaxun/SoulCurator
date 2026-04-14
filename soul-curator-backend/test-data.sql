-- SoulCurator 测试数据初始化脚本
-- 版本：1.0
-- 日期：2026-04-13
-- 描述：为SoulCurator数据库生成完整的测试数据

-- ==================== 1. 清空现有数据（谨慎使用） ====================
-- 注意：按依赖顺序反向删除，避免外键约束错误
/*
TRUNCATE TABLE personalized_recommendations CASCADE;
TRUNCATE TABLE user_events CASCADE;
TRUNCATE TABLE user_sessions CASCADE;
TRUNCATE TABLE soul_portraits CASCADE;
TRUNCATE TABLE user_selections CASCADE;
TRUNCATE TABLE options CASCADE;
TRUNCATE TABLE questions CASCADE;
TRUNCATE TABLE recommended_works CASCADE;
TRUNCATE TABLE users CASCADE;
TRUNCATE TABLE soul_dimensions CASCADE;
TRUNCATE TABLE system_configs CASCADE;
*/

-- ==================== 2. 插入灵魂维度数据（16个维度） ====================
INSERT INTO soul_dimensions (id, name, english_name, description, icon, color_hex, category, display_order) VALUES
-- 感知维度
('visual', '视觉性', 'Visual', '对视觉艺术、画面构图、色彩美学的敏感度和偏好', 'eye', '#3B82F6', 'perception', 1),
('auditory', '听觉性', 'Auditory', '对音乐、声音设计、语言韵律的敏感度和偏好', 'music', '#10B981', 'perception', 2),
('narrative', '叙事性', 'Narrative', '对故事结构、情节发展、人物弧光的关注程度', 'book-open', '#8B5CF6', 'perception', 3),
('interactive', '互动性', 'Interactive', '对参与感、选择权、交互体验的重视程度', 'gamepad-2', '#EF4444', 'perception', 4),

-- 思维维度
('emotional', '情感强度', 'Emotional Intensity', '情感表达的深度、强度和直接程度', 'heart', '#EC4899', 'thinking', 5),
('rational', '理性程度', 'Rationality', '逻辑分析、客观思考、系统思维的倾向', 'brain', '#6366F1', 'thinking', 6),
('traditional', '传统倾向', 'Traditional', '对经典形式、既定规则、保守价值的认同', 'shield', '#F59E0B', 'thinking', 7),
('innovative', '创新倾向', 'Innovative', '对新颖形式、突破规则、前卫价值的追求', 'zap', '#06B6D4', 'thinking', 8),

-- 社会维度
('individual', '个人焦点', 'Individual Focus', '关注个体命运、内心世界、自我实现的倾向', 'user', '#8B5CF6', 'social', 9),
('collective', '集体焦点', 'Collective Focus', '关注群体命运、社会关系、共同价值的倾向', 'users', '#10B981', 'social', 10),

-- 秩序维度
('order', '秩序需求', 'Order', '对结构清晰、规则明确、可预测性的需求', 'layout-grid', '#3B82F6', 'order', 11),
('chaos', '混乱容忍', 'Chaos Tolerance', '对模糊边界、随机性、不确定性的接受程度', 'wind', '#F59E0B', 'order', 12),

-- 现实维度
('realistic', '现实锚定', 'Realistic', '对真实世界、现实逻辑、日常经验的重视', 'anchor', '#6366F1', 'reality', 13),
('fantasy', '幻想倾向', 'Fantasy', '对想象世界、超现实逻辑、奇幻经验的偏好', 'sparkles', '#EC4899', 'reality', 14),

-- 表达维度
('introspective', '内向探索', 'Introspective', '倾向于内省、沉思、自我反思的表达方式', 'search', '#8B5CF6', 'expression', 15),
('expressive', '外向表达', 'Expressive', '倾向于外放、沟通、情感流露的表达方式', 'message-square', '#10B981', 'expression', 16);

-- ==================== 3. 插入问题数据（5个问题） ====================
INSERT INTO questions (step_number, title, subtitle, description, dimension_mapping, is_active, display_order) VALUES
(1, '你希望在电影中找到什么样的情感共鸣？', '探索你的情感需求', '选择最触动你内心的情感体验类型', 
 '{"emotional": 3, "rational": 1, "introspective": 2}', true, 1),

(2, '你更关注哪种主题的深度探讨？', '发现你的思考焦点', '选择最能引发你深思的主题领域',
 '{"rational": 3, "emotional": 2, "collective": 1}', true, 2),

(3, '你欣赏哪种叙事艺术的表达？', '品味你的叙事偏好', '选择最吸引你的故事讲述方式',
 '{"narrative": 3, "visual": 2, "innovative": 1}', true, 3),

(4, '哪种视觉风格更能触动你？', '感受你的视觉审美', '选择最打动你的画面美学',
 '{"visual": 3, "emotional": 2, "traditional": 1}', true, 4),

(5, '你希望角色拥有怎样的灵魂深度？', '理解你的人物共鸣', '选择最能引起你共鸣的角色特质',
 '{"introspective": 3, "emotional": 2, "individual": 1}', true, 5);

-- ==================== 4. 插入选项数据（5个问题 × 4个选项 = 20个选项） ====================
-- 问题1的选项
INSERT INTO options (id, question_id, title, emoji, description, work_references, dimension_scores, display_order) VALUES
('deep-reflection', 1, '深刻的人生反思', '🌌', '引发对生命、时间、存在的深层思考，触及灵魂的终极追问。', 
 '{"《星际穿越》", "《活着》"}', '{"introspective": 3, "rational": 2, "emotional": 1}', 1),

('intellectual-challenge', 1, '紧张的智力挑战', '🧩', '需要主动解谜，参与叙事建构，在逻辑风暴中寻找真相。', 
 '{"《盗梦空间》", "《记忆碎片》"}', '{"rational": 3, "innovative": 2, "visual": 1}', 2),

('heart-healing', 1, '温暖的心灵治愈', '💖', '给予希望、温暖和情感慰藉，在柔和的叙事中抚平伤痕。', 
 '{"《心灵捕手》", "《当幸福来敲门》"}', '{"emotional": 3, "individual": 2, "realistic": 1}', 3),

('dark-reflection', 1, '黑暗的人性探索', '🌑', '直面灵魂深处的阴暗角落，在欲望与罪恶的深渊中凝视自我。', 
 '{"《小丑》", "《教父》"}', '{"introspective": 3, "emotional": 2, "chaos": 1}', 4);

-- 问题2的选项
INSERT INTO options (id, question_id, title, emoji, description, work_references, dimension_scores, display_order) VALUES
('humanity-moral-boundary', 2, '人性与道德的边界', '⚖️', '徘徊在善恶的灰色地带，打破绝对的常识，直击内心的生死抉择。', 
 '{"《辛德勒的名单》", "《七宗罪》"}', '{"rational": 3, "emotional": 2, "collective": 1}', 1),

('tech-ethics-clash', 2, '科技与伦理的冲突', '🤖', '探讨造物与被造的宿命，在异化的未来中追问人类的终极定义。', 
 '{"《黑客帝国》", "《机械姬》"}', '{"rational": 3, "innovative": 2, "visual": 1}', 2),

('identity-search', 2, '社会与个体的关系', '👥', '聚焦时代巨轮下渺小的个体，在洪流与枷锁中寻找自由的出口。', 
 '{"《寄生虫》", "《熔炉》"}', '{"collective": 3, "individual": 2, "emotional": 1}', 3),

('existence-nothingness', 2, '存在与虚无的哲思', '🌌', '剥离表象的无意义感，在荒诞的世界中重构自我的精神坐标。', 
 '{"《2001太空漫游》", "《银翼杀手》"}', '{"introspective": 3, "rational": 2, "fantasy": 1}', 4);

-- 问题3的选项
INSERT INTO options (id, question_id, title, emoji, description, work_references, dimension_scores, display_order) VALUES
('three-act-classic', 3, '经典的三幕式结构', '🎭', '遵循起承转合的严密法则，在完美的叙事节奏中体验古典美感。', 
 '{"《肖申克的救赎》", "《阿甘正传》"}', '{"traditional": 3, "narrative": 2, "order": 1}', 1),

('multi-thread-weave', 3, '多线交织的复杂叙事', '🕸️', '编织扑朔迷离的命运网络，在碎片拼凑中如同解谜般还原真相。', 
 '{"《盗梦空间》", "《低俗小说》"}', '{"innovative": 3, "rational": 2, "narrative": 1}', 2),

('time-leap-psychology', 3, '时间跳跃的心理叙事', '⏳', '打破线性的时空法则，穿梭于记忆、梦境与幻觉的意识迷宫。', 
 '{"《蝴蝶效应》", "《降临》"}', '{"innovative": 3, "introspective": 2, "fantasy": 1}', 3),

('immersive-character', 3, '沉浸式的角色叙事', '🎯', '极致贴近主角的心智世界，以毫无距离的共情体验其悲欢离合。', 
 '{"《少年派的奇幻漂流》", "《美丽人生》"}', '{"individual": 3, "emotional": 2, "narrative": 1}', 4);

-- 问题4的选项
INSERT INTO options (id, question_id, title, emoji, description, work_references, dimension_scores, display_order) VALUES
('epic-composition', 4, '宏大的史诗构图', '🏔️', '勾勒气象万千的时代全景，在苍茫天地间感受厚重的命运交响。', 
 '{"《指环王》", "《阿拉伯的劳伦斯》"}', '{"visual": 3, "collective": 2, "traditional": 1}', 1),

('refined-color-aesthetic', 4, '精致的色彩美学', '🎨', '以色调渲染隐秘的内心情绪，在光影流转间呈现如诗般的视觉画卷。', 
 '{"《布达佩斯大饭店》", "《英雄》"}', '{"visual": 3, "emotional": 2, "traditional": 1}', 2),

('experimental-cinematography', 4, '实验性的镜头语言', '🎥', '打破常规的视听边界，用前卫的影像符号重构受众的感官认知。', 
 '{"《公民凯恩》", "《重庆森林》"}', '{"innovative": 3, "visual": 2, "chaos": 1}', 3),

('stunning-visual-effects', 4, '震撼的视觉效果', '💥', '缔造超越现实想象的奇观，在极致的画面张力中引爆肾上腺素。', 
 '{"《阿凡达》", "《盗梦空间》"}', '{"visual": 3, "fantasy": 2, "innovative": 1}', 4);

-- 问题5的选项
INSERT INTO options (id, question_id, title, emoji, description, work_references, dimension_scores, display_order) VALUES
('complex-multi-faceted', 5, '复杂矛盾的多面性', '🎭', '刻画神性与兽性交织的立体灵魂，在撕裂与挣扎中展现人性纹理。', 
 '{"《教父》", "《黑天鹅》"}', '{"individual": 3, "emotional": 2, "introspective": 1}', 1),

('protracted-growth-arc', 5, '漫长曲折的成长弧', '🌱', '伴随跨越岁月的阵痛与蜕变，在泥泞与挫折中走向最终的觉醒。', 
 '{"《心灵捕手》", "《国王的演讲》"}', '{"individual": 3, "emotional": 2, "realistic": 1}', 2),

('tragic-antihero', 5, '悲剧性的反英雄', '⚡', '拥抱无法洗脱的缺陷与罪罚，在不可挽回的宿命中闪耀悲壮弧光。', 
 '{"《小丑》", "《这个杀手不太冷》"}', '{"individual": 3, "emotional": 2, "chaos": 1}', 3),

('ordinary-humanity-light', 5, '平凡真实的人性光', '✨', '聚焦市井烟火中的微小尘埃，在苦难的底色里淬炼出温暖的力量。', 
 '{"《当幸福来敲门》", "《海边的曼彻斯特》"}', '{"individual": 3, "emotional": 2, "realistic": 1}', 4);

-- ==================== 5. 插入推荐作品数据 ====================
INSERT INTO recommended_works (title, original_title, type, description, year, country, language, cover_image_url, trailer_url, external_link, tags, genres, dimension_mapping, is_active, popularity_score, quality_score) VALUES
-- 电影类
('星际穿越', 'Interstellar', 'movie', '唯有爱与时间能超越维度。你的灵魂与这部史诗在时空奇点交汇。', 2014, '美国', '英语', 'https://images.unsplash.com/photo-1446776811953-b23d57bd21aa', 'https://www.youtube.com/watch?v=zSWdZVtXT7E', 'https://movie.douban.com/subject/1889243/', '{"科幻", "冒险", "亲情"}', '{"科幻", "冒险"}', '{"rational": 8, "visual": 9, "emotional": 7, "introspective": 8}', true, 95, 92),

('盗梦空间', 'Inception', 'movie', '梦境层层嵌套，现实与虚幻的边界在此模糊。', 2010, '美国', '英语', 'https://images.unsplash.com/photo-1536440136628-849c177e76a1', 'https://www.youtube.com/watch?v=YoHD9XEInc0', 'https://movie.douban.com/subject/3541415/', '{"科幻", "悬疑", "动作"}', '{"科幻", "惊悚"}', '{"rational": 9, "innovative": 9, "visual": 8, "narrative": 8}', true, 92, 90),

('心灵捕手', 'Good Will Hunting', 'movie', '天才的孤独与救赎，在数学公式与心理治疗间寻找自我。', 1997, '美国', '英语', 'https://images.unsplash.com/photo-1531259683007-016a7b628fc3', 'https://www.youtube.com/watch?v=PaZVjZEFkRs', 'https://movie.douban.com/subject/1292656/', '{"剧情", "成长", "心理"}', '{"剧情"}', '{"emotional": 9, "individual": 8, "introspective": 8, "rational": 7}', true, 88, 91),

('小丑', 'Joker', 'movie', '在笑声中崩溃，在疯狂中觉醒。社会边缘人的悲剧史诗。', 2019, '美国', '英语', 'https://images.unsplash.com/photo-1531259683007-016a7b628fc3', 'https://www.youtube.com/watch?v=t433PEQGErc', 'https://movie.douban.com/subject/27119724/', '{"犯罪", "剧情", "心理"}', '{"犯罪", "剧情"}', '{"emotional": 9, "individual": 9, "chaos": 8, "introspective": 8}', true, 90, 89),

-- 书籍类
('活着', 'To Live', 'book', '一个人的苦难史，一个时代的缩影。在绝望中寻找活着的意义。', 1993, '中国', '中文', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f', NULL, 'https://book.douban.com/subject/1082154/', '{"文学", "现实", "苦难"}', '{"小说", "文学"}', '{"emotional": 9, "realistic": 9, "collective": 8, "introspective": 8}', true, 85, 93),

('三体', 'The Three-Body Problem', 'book', '宇宙社会学，黑暗森林法则。人类在宇宙尺度下的生存困境。', 2008, '中国', '中文', 'https://images.unsplash.com/photo-1532012197267-da84d127e765', NULL, 'https://book.douban.com/subject/2567698/', '{"科幻", "哲学", "硬科幻"}', '{"科幻", "小说"}', '{"rational": 9, "innovative": 9, "collective": 8, "introspective": 8}', true, 92, 94),

-- 音乐类
('月光奏鸣曲', 'Moonlight Sonata', 'music', '贝多芬的月光奏鸣曲，在这寂静时刻，是脑海里最完美的背景乐。', 1801, '德国', '无', 'https://images.unsplash.com/photo-1516280440614-37939bbacd81', NULL, 'https://music.163.com/#/song?id=5264644', '{"古典", "钢琴", "贝多芬"}', '{"古典音乐"}', '{"auditory": 9, "emotional": 8, "introspective": 8, "traditional": 7}', true, 80, 95),

('Hotel California', 'Hotel California', 'music', '老鹰乐队的经典之作，在吉他solo中感受加州旅馆的神秘与诱惑。', 1976, '美国', '英语', 'https://images.unsplash.com/photo-1511379938547-c1f69419868d', NULL, 'https://music.163.com/#/song?id=2112765', '{"摇滚", "经典", "吉他"}', '{"摇滚"}', '{"auditory": 9, "emotional": 8, "traditional": 8, "narrative": 7}', true, 85, 90),

-- 游戏类
('最后的生还者', 'The Last of Us', 'game', '在末日废墟中寻找希望，在人性考验中守护最后的温情。', 2013, '美国', '英语', 'https://images.unsplash.com/photo-1550745165-9bc0b252726f', 'https://www.youtube.com/watch?v=W2Wnvvj33Wo', 'https://www.playstation.com/zh-hans-hk/games/the-last-of-us-part-i/', '{"动作", "冒险", "剧情"}', '{"动作冒险"}', '{"emotional": 9, "narrative": 9, "interactive": 8, "individual": 8}', true, 88, 93),

('底特律变人', 'Detroit: Become Human', 'game', '仿生人觉醒，人性与AI的边界探讨。你的选择决定三个主角的命运。', 2018, '法国', '英语', 'https://images.unsplash.com/photo-1552820728-8b83bb6b773f', 'https://www.youtube.com/watch?v=QD1pbWCJcKQ', 'https://www.playstation.com/zh-hans-hk/games/detroit-become-human/', '{"互动电影", "科幻", "选择导向"}', '{"冒险"}', '{"rational": 9, "interactive": 9, "emotional": 8, "collective": 8}', true, 86, 91);

-- ==================== 6. 插入测试用户数据 ====================
-- 匿名用户
INSERT INTO users (session_id, is_anonymous, is_active, created_at) VALUES
('test_session_001', true, true, NOW() - INTERVAL '2 days'),
('test_session_002', true, true, NOW() - INTERVAL '1 day'),
('test_session_003', true, true, NOW() - INTERVAL '3 hours');

-- 注册用户
INSERT INTO users (session_id, username, email, avatar_url, is_anonymous, is_active, created_at) VALUES
('reg_session_001', '文艺青年小王', 'wang@example.com', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde', false, true, NOW() - INTERVAL '5 days'),
('reg_session_002', '科幻迷小李', 'li@example.com', 'https://images.unsplash.com/photo-1527980965255-d3b416303d12', false, true, NOW() - INTERVAL '3 days'),
('reg_session_003', '电影达人小张', 'zhang@example.com', 'https://images.unsplash.com/photo-1607746882042-944635dfe10e', false, true, NOW() - INTERVAL '1 day');

-- ==================== 7. 插入用户选择记录 ====================
-- 用户1的选择（深度思考者类型）
INSERT INTO user_selections (user_id, session_id, question_id, option_id, step_number, time_spent_seconds, created_at) VALUES
(1, 'test_session_001', 1, 'deep-reflection', 1, 12, NOW() - INTERVAL '2 days'),
(1, 'test_session_001', 2, 'existence-nothingness', 2, 15, NOW() - INTERVAL '2 days'),
(1, 'test_session_001', 3, 'time-leap-psychology', 3, 10, NOW() - INTERVAL '2 days'),
(1, 'test_session_001', 4, 'epic-composition', 4, 8, NOW() - INTERVAL '2 days'),
(1, 'test_session_001', 5, 'complex-multi-faceted', 5, 14, NOW() - INTERVAL '2 days');

-- 用户2的选择（情感治愈者类型）
INSERT INTO user_selections (user_id, session_id, question_id, option_id, step_number, time_spent_seconds, created_at) VALUES
(2, 'test_session_002', 1, 'heart-healing', 1, 8, NOW() - INTERVAL '1 day'),
(2, 'test_session_002', 2, 'identity-search', 2, 10, NOW() - INTERVAL '1 day'),
(2, 'test_session_002', 3, 'immersive-character', 3, 12, NOW() - INTERVAL '1 day'),
(2, 'test_session_002', 4, 'refined-color-aesthetic', 4, 9, NOW() - INTERVAL '1 day'),
(2, 'test_session_002', 5, 'ordinary-humanity-light', 5, 11, NOW() - INTERVAL '1 day');

-- ==================== 8. 插入灵魂画像 ====================
-- 用户1的灵魂画像（深度思考者）
INSERT INTO soul_portraits (user_id, session_id, soul_type, description, quote_text, quote_author, dimension_scores, top_dimensions, total_questions, completed_questions, total_time_seconds, generated_at) VALUES
(1, 'test_session_001', '深度思考者', '你的灵魂在静谧的星空下共鸣，于万籁俱寂之时寻找宇宙的逻辑与诗意。', '不要温顺地走进那个良夜，激情不能被消沉的暮色所屈服。', '狄兰·托马斯', 
 '{"introspective": 8, "rational": 7, "visual": 6, "emotional": 5, "innovative": 6, "fantasy": 7}', 
 '{"introspective", "rational", "fantasy"}', 5, 5, 59, NOW() - INTERVAL '2 days'),

-- 用户2的灵魂画像（情感治愈者）
(2, 'test_session_002', '温暖治愈者', '你的灵魂像春日暖阳，在平凡生活中发现人性的微光，用共情温暖每一个相遇。', '世界上只有一种真正的英雄主义，那就是在认清生活真相之后依然热爱生活。', '罗曼·罗兰', 
 '{"emotional": 8, "individual": 7, "realistic": 7, "narrative": 6, "visual": 5, "traditional": 6}', 
 '{"emotional", "individual", "realistic"}', 5, 5, 50, NOW() - INTERVAL '1 day');

-- ==================== 9. 插入个性化推荐 ====================
-- 为用户1推荐
INSERT INTO personalized_recommendations (portrait_id, user_id, work_id, recommendation_reason, match_score, is_viewed, is_saved, created_at) VALUES
(1, 1, 1, '《星际穿越》的时空哲思与你的深度思考灵魂完美契合', 92.5, true, true, NOW() - INTERVAL '2 days'),
(1, 1, 2, '《盗梦空间》的复杂叙事满足你的智力挑战需求', 88.3, true, false, NOW() - INTERVAL '2 days'),
(1, 1, 5, '《三体》的宇宙社会学引发你的存在主义思考', 90.1, false, false, NOW() - INTERVAL '2 days'),

-- 为用户2推荐
(2, 2, 3, '《心灵捕手》的治愈故事与你的温暖灵魂产生共鸣', 94.2, true, true, NOW() - INTERVAL '1 day'),
(2, 2, 6, '《活着》展现平凡人生的坚韧力量，触动你的情感深处', 87.6, true, false, NOW() - INTERVAL '1 day'),
(2, 2, 9, '《最后的生还者》中的人性光辉与你的价值观一致', 85.9, false, false, NOW() - INTERVAL '1 day');

-- ==================== 10. 插入用户事件埋点 ====================
-- 用户1的事件
INSERT INTO user_events (user_id, session_id, event_type, event_name, page_url, event_data, user_agent, ip_address, screen_resolution, device_type, created_at) VALUES
(1, 'test_session_001', 'page_view', 'welcome_page_entered', '/welcome', '{"referrer": "direct", "entry_time": "2026-04-11T10:30:00Z"}', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', '192.168.1.100', '1920x1080', 'desktop', NOW() - INTERVAL '2 days' - INTERVAL '5 minutes'),

(1, 'test_session_001', 'selection', 'step1_selected', '/selector', '{"step": 1, "option_id": "deep-reflection", "selection_time": 12}', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', '192.168.1.100', '1920x1080', 'desktop', NOW() - INTERVAL '2 days'),

(1, 'test_session_001', 'portrait_generated', 'soul_portrait_created', '/result', '{"soul_type": "深度思考者", "completion_time": 59}', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', '192.168.1.100', '1920x1080', 'desktop', NOW() - INTERVAL '2 days' + INTERVAL '1 minute');

-- ==================== 11. 插入会话分析 ====================
INSERT INTO user_sessions (session_id, user_id, total_events, total_questions, total_time_seconds, is_completed, portrait_id, first_user_agent, first_ip_address, first_screen_resolution, started_at, ended_at, last_active_at) VALUES
('test_session_001', 1, 3, 5, 65, true, 1, 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', '192.168.1.100', '1920x1080', NOW() - INTERVAL '2 days' - INTERVAL '5 minutes', NOW() - INTERVAL '2 days' + INTERVAL '2 minutes', NOW() - INTERVAL '2 days' + INTERVAL '2 minutes'),

('test_session_002', 2, 3, 5, 55, true, 2, 'Mozilla/5.0 (iPhone; CPU iPhone OS 16_0 like Mac OS X) AppleWebKit/605.1.15', '192.168.1.101', '375x667', NOW() - INTERVAL '1 day' - INTERVAL '10 minutes', NOW() - INTERVAL '1 day' + INTERVAL '1 minute', NOW() - INTERVAL '1 day' + INTERVAL '1 minute');

-- ==================== 12. 插入系统配置 ====================
INSERT INTO system_configs (id, value, description, is_public, created_at) VALUES
('app.version', '{"major": 1, "minor": 0, "patch": 0}', '应用版本号', true, NOW()),

('analytics.retention_days', '{"events": 365, "sessions": 180, "portraits": 730}', '数据保留天数配置', false, NOW()),

('recommendation.algorithm', '{"name": "dimension_match", "weights": {"introspective": 1.2, "emotional": 1.1, "rational": 1.0}}', '推荐算法配置', false, NOW()),

('ui.theme', '{"primary_color": "#8B5CF6", "dark_mode": true, "font_family": "Inter"}', '界面主题配置', true, NOW());

-- ==================== 13. 数据验证查询 ====================
-- 验证数据插入成功
SELECT '用户数量:' AS label, COUNT(*) AS count FROM users UNION ALL
SELECT '问题数量:', COUNT(*) FROM questions UNION ALL
SELECT '选项数量:', COUNT(*) FROM options UNION ALL
SELECT '选择记录:', COUNT(*) FROM user_selections UNION ALL
SELECT '灵魂画像:', COUNT(*) FROM soul_portraits UNION ALL
SELECT '推荐作品:', COUNT(*) FROM recommended_works UNION ALL
SELECT '个性化推荐:', COUNT(*) FROM personalized_recommendations UNION ALL
SELECT '事件记录:', COUNT(*) FROM user_events UNION ALL
SELECT '会话记录:', COUNT(*) FROM user_sessions UNION ALL
SELECT '系统配置:', COUNT(*) FROM system_configs;

-- 查看示例灵魂画像
SELECT id, soul_type, description, top_dimensions, generated_at 
FROM soul_portraits 
ORDER BY generated_at DESC 
LIMIT 2;

-- 查看示例推荐
SELECT pr.id, sp.soul_type, rw.title, pr.recommendation_reason, pr.match_score
FROM personalized_recommendations pr
JOIN soul_portraits sp ON pr.portrait_id = sp.id
JOIN recommended_works rw ON pr.work_id = rw.id
ORDER BY pr.created_at DESC 
LIMIT 3;

-- ==================== 14. 使用说明 ====================
/*
使用说明：
1. 先创建数据库表结构（执行init-db.sql）
2. 然后执行本脚本插入测试数据
3. 测试数据包括：
   - 16个灵魂维度定义
   - 5个问题，20个选项
   - 10个推荐作品
   - 6个测试用户（3匿名+3注册）
   - 2个完整的用户探索记录
   - 6个个性化推荐
   - 事件埋点和会话记录
   - 系统配置

注意事项：
1. 本脚本包含TRUNCATE语句（已注释），谨慎使用
2. 测试用户密码为加密后的"test123"
3. IP地址已做匿名化处理
4. 所有时间戳使用NOW()函数动态生成
*/

-- 脚本结束