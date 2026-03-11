package com.example.feiyi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.feiyi.common.Result;
import com.example.feiyi.annotation.Log;
import com.example.feiyi.entity.*;
import com.example.feiyi.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private UserMapper userMapper;
    @Autowired private HeritageProjectMapper projectMapper;
    @Autowired private HeritageCategoryMapper categoryMapper;
    @Autowired private InheritorMapper inheritorMapper;
    @Autowired private ActivityMapper activityMapper;
    @Autowired private ActivityReservationMapper reservationMapper;
    @Autowired private ProductMapper productMapper;
    @Autowired private ProductCategoryMapper productCategoryMapper;
    @Autowired private OrderMapper orderMapper;
    @Autowired private OrderItemMapper orderItemMapper;
    @Autowired private MessageBoardMapper messageMapper;
    @Autowired private BannerMapper bannerMapper;
    @Autowired private OperationLogMapper logMapper;
    @Autowired private PasswordEncoder passwordEncoder;

    // ============ 仪表盘统计 ============
    @GetMapping("/dashboard")
    public Result<?> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("userCount", userMapper.selectCount(null));
        data.put("projectCount", projectMapper.selectCount(null));
        data.put("inheritorCount", inheritorMapper.selectCount(null));
        data.put("activityCount", activityMapper.selectCount(null));
        data.put("productCount", productMapper.selectCount(null));
        data.put("orderCount", orderMapper.selectCount(null));

        BigDecimal totalSales = BigDecimal.ZERO;
        List<Order> orders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>().ne(Order::getStatus, 4));
        for (Order o : orders) {
            totalSales = totalSales.add(o.getTotalAmount());
        }
        data.put("totalSales", totalSales);

        data.put("pendingMessages", messageMapper.selectCount(
                new LambdaQueryWrapper<MessageBoard>().eq(MessageBoard::getStatus, 0)));

        // 近7天订单统计
        List<Map<String, Object>> orderStats = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime start = LocalDateTime.now().minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime end = start.plusDays(1);
            Long count = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                    .ge(Order::getCreateTime, start).lt(Order::getCreateTime, end));
            Map<String, Object> stat = new HashMap<>();
            stat.put("date", start.toLocalDate().toString());
            stat.put("count", count);
            orderStats.add(stat);
        }
        data.put("orderStats", orderStats);

        // 近7天用户注册统计
        List<Map<String, Object>> userStats = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime start = LocalDateTime.now().minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime end = start.plusDays(1);
            Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                    .ge(User::getCreateTime, start).lt(User::getCreateTime, end));
            Map<String, Object> stat = new HashMap<>();
            stat.put("date", start.toLocalDate().toString());
            stat.put("count", count);
            userStats.add(stat);
        }
        data.put("userStats", userStats);

        // 近7天销售额统计
        List<Map<String, Object>> salesStats = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime start = LocalDateTime.now().minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime end = start.plusDays(1);
            List<Order> dayOrders = orderMapper.selectList(new LambdaQueryWrapper<Order>()
                    .ne(Order::getStatus, 4)
                    .ge(Order::getCreateTime, start).lt(Order::getCreateTime, end));
            BigDecimal daySales = BigDecimal.ZERO;
            for (Order o : dayOrders) {
                daySales = daySales.add(o.getTotalAmount());
            }
            Map<String, Object> stat = new HashMap<>();
            stat.put("date", start.toLocalDate().toString());
            stat.put("amount", daySales);
            salesStats.add(stat);
        }
        data.put("salesStats", salesStats);

        return Result.success(data);
    }

    // ============ 用户管理 ============
    @GetMapping("/user/list")
    public Result<?> userList(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(required = false) String keyword) {
        Page<User> p = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword).or().like(User::getNickname, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);
        userMapper.selectPage(p, wrapper);
        p.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(p);
    }

    @Log("修改用户状态")
    @PutMapping("/user/status")
    public Result<?> updateUserStatus(@RequestBody User user) {
        User u = new User();
        u.setId(user.getId());
        u.setStatus(user.getStatus());
        userMapper.updateById(u);
        return Result.success();
    }

    @Log("新增/编辑用户")
    @PostMapping("/user")
    public Result<?> saveUser(@RequestBody User user) {
        if (user.getId() == null) {
            User existing = userMapper.selectOne(
                    new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
            if (existing != null) return Result.error("用户名已存在");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userMapper.insert(user);
        } else {
            if (StringUtils.hasText(user.getPassword())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                user.setPassword(null);
            }
            userMapper.updateById(user);
        }
        return Result.success();
    }

    @Log("删除用户")
    @DeleteMapping("/user/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        userMapper.deleteById(id);
        return Result.success();
    }

    // ============ 非遗项目管理 ============
    @GetMapping("/heritage/list")
    public Result<?> heritageList(@RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(required = false) String keyword) {
        Page<HeritageProject> p = new Page<>(page, size);
        LambdaQueryWrapper<HeritageProject> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(HeritageProject::getTitle, keyword);
        }
        wrapper.orderByDesc(HeritageProject::getCreateTime);
        projectMapper.selectPage(p, wrapper);
        return Result.success(p);
    }

    @Log("新增/编辑非遗项目")
    @PostMapping("/heritage")
    public Result<?> saveHeritage(@RequestBody HeritageProject project) {
        if (project.getId() == null) {
            projectMapper.insert(project);
        } else {
            projectMapper.updateById(project);
        }
        return Result.success();
    }

    @Log("删除非遗项目")
    @DeleteMapping("/heritage/{id}")
    public Result<?> deleteHeritage(@PathVariable Long id) {
        projectMapper.deleteById(id);
        return Result.success();
    }

    // ============ 非遗分类管理 ============
    @GetMapping("/category/list")
    public Result<?> categoryList() {
        return Result.success(categoryMapper.selectList(
                new LambdaQueryWrapper<HeritageCategory>().orderByAsc(HeritageCategory::getSortOrder)));
    }

    @Log("新增/编辑非遗分类")
    @PostMapping("/category")
    public Result<?> saveCategory(@RequestBody HeritageCategory category) {
        if (category.getId() == null) {
            categoryMapper.insert(category);
        } else {
            categoryMapper.updateById(category);
        }
        return Result.success();
    }

    @Log("删除非遗分类")
    @DeleteMapping("/category/{id}")
    public Result<?> deleteCategory(@PathVariable Long id) {
        categoryMapper.deleteById(id);
        return Result.success();
    }

    // ============ 传承人管理 ============
    @GetMapping("/inheritor/list")
    public Result<?> inheritorList(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String keyword) {
        Page<Inheritor> p = new Page<>(page, size);
        LambdaQueryWrapper<Inheritor> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Inheritor::getName, keyword);
        }
        wrapper.orderByDesc(Inheritor::getCreateTime);
        inheritorMapper.selectPage(p, wrapper);
        return Result.success(p);
    }

    @Log("新增/编辑传承人")
    @PostMapping("/inheritor")
    public Result<?> saveInheritor(@RequestBody Inheritor inheritor) {
        if (inheritor.getId() == null) {
            inheritorMapper.insert(inheritor);
        } else {
            inheritorMapper.updateById(inheritor);
        }
        return Result.success();
    }

    @Log("删除传承人")
    @DeleteMapping("/inheritor/{id}")
    public Result<?> deleteInheritor(@PathVariable Long id) {
        inheritorMapper.deleteById(id);
        return Result.success();
    }

    // ============ 活动管理 ============
    @GetMapping("/activity/list")
    public Result<?> activityList(@RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        Page<Activity> p = new Page<>(page, size);
        activityMapper.selectPage(p, new LambdaQueryWrapper<Activity>()
                .orderByDesc(Activity::getCreateTime));
        return Result.success(p);
    }

    @Log("新增/编辑活动")
    @PostMapping("/activity")
    public Result<?> saveActivity(@RequestBody Activity activity) {
        if (activity.getId() == null) {
            activityMapper.insert(activity);
        } else {
            activityMapper.updateById(activity);
        }
        return Result.success();
    }

    @Log("删除活动")
    @DeleteMapping("/activity/{id}")
    public Result<?> deleteActivity(@PathVariable Long id) {
        activityMapper.deleteById(id);
        return Result.success();
    }

    // ============ 预约管理 ============
    @GetMapping("/reservation/list")
    public Result<?> reservationList(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(required = false) Long activityId) {
        Page<ActivityReservation> p = new Page<>(page, size);
        LambdaQueryWrapper<ActivityReservation> wrapper = new LambdaQueryWrapper<>();
        if (activityId != null) {
            wrapper.eq(ActivityReservation::getActivityId, activityId);
        }
        wrapper.orderByDesc(ActivityReservation::getCreateTime);
        reservationMapper.selectPage(p, wrapper);
        return Result.success(p);
    }

    @Log("审核预约")
    @PutMapping("/reservation/audit")
    public Result<?> auditReservation(@RequestBody ActivityReservation reservation) {
        ActivityReservation r = new ActivityReservation();
        r.setId(reservation.getId());
        r.setStatus(reservation.getStatus());
        r.setRemark(reservation.getRemark());
        reservationMapper.updateById(r);
        return Result.success();
    }

    @Log("导出预约名单")
    @GetMapping("/reservation/export")
    public void exportReservations(@RequestParam(required = false) Long activityId,
                                   HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<ActivityReservation> wrapper = new LambdaQueryWrapper<>();
        if (activityId != null) {
            wrapper.eq(ActivityReservation::getActivityId, activityId);
        }
        wrapper.orderByDesc(ActivityReservation::getCreateTime);
        List<ActivityReservation> dataList = reservationMapper.selectList(wrapper);

        // 查询活动名称映射
        Map<Long, String> activityNames = new HashMap<>();
        List<Activity> activities = activityMapper.selectList(null);
        for (Activity a : activities) {
            activityNames.put(a.getId(), a.getTitle());
        }

        // 查询用户名映射
        Map<Long, String> userNames = new HashMap<>();
        List<User> users = userMapper.selectList(null);
        for (User u : users) {
            userNames.put(u.getId(), u.getUsername());
        }

        String[] statusTexts = {"待审核", "已通过", "已拒绝", "已取消"};
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("预约名单");

        // 表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        String[] headers = {"序号", "活动名称", "预约人", "用户账号", "联系电话", "状态", "备注", "预约时间"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 数据行
        for (int i = 0; i < dataList.size(); i++) {
            ActivityReservation item = dataList.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(activityNames.getOrDefault(item.getActivityId(), "活动#" + item.getActivityId()));
            row.createCell(2).setCellValue(item.getRealName() != null ? item.getRealName() : "");
            row.createCell(3).setCellValue(userNames.getOrDefault(item.getUserId(), "用户#" + item.getUserId()));
            row.createCell(4).setCellValue(item.getPhone() != null ? item.getPhone() : "");
            int st = item.getStatus() != null ? item.getStatus() : 0;
            row.createCell(5).setCellValue(st < statusTexts.length ? statusTexts[st] : "未知");
            row.createCell(6).setCellValue(item.getRemark() != null ? item.getRemark() : "");
            row.createCell(7).setCellValue(item.getCreateTime() != null ? item.getCreateTime().format(dtf) : "");
        }

        // 自动列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i), 3000));
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=reservations.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    // ============ 商品管理 ============
    @GetMapping("/product/list")
    public Result<?> productList(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String keyword) {
        Page<Product> p = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getName, keyword);
        }
        wrapper.orderByDesc(Product::getCreateTime);
        productMapper.selectPage(p, wrapper);
        return Result.success(p);
    }

    @Log("新增/编辑商品")
    @PostMapping("/product")
    public Result<?> saveProduct(@RequestBody Product product) {
        if (product.getId() == null) {
            productMapper.insert(product);
        } else {
            productMapper.updateById(product);
        }
        return Result.success();
    }

    @Log("删除商品")
    @DeleteMapping("/product/{id}")
    public Result<?> deleteProduct(@PathVariable Long id) {
        productMapper.deleteById(id);
        return Result.success();
    }

    // ============ 商品分类管理 ============
    @GetMapping("/product-category/list")
    public Result<?> productCategoryList() {
        return Result.success(productCategoryMapper.selectList(
                new LambdaQueryWrapper<ProductCategory>().orderByAsc(ProductCategory::getSortOrder)));
    }

    @Log("新增/编辑商品分类")
    @PostMapping("/product-category")
    public Result<?> saveProductCategory(@RequestBody ProductCategory category) {
        if (category.getId() == null) {
            productCategoryMapper.insert(category);
        } else {
            productCategoryMapper.updateById(category);
        }
        return Result.success();
    }

    @Log("删除商品分类")
    @DeleteMapping("/product-category/{id}")
    public Result<?> deleteProductCategory(@PathVariable Long id) {
        productCategoryMapper.deleteById(id);
        return Result.success();
    }

    // ============ 订单管理 ============
    @GetMapping("/order/list")
    public Result<?> orderList(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(required = false) Integer status) {
        Page<Order> p = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        orderMapper.selectPage(p, wrapper);
        for (Order o : p.getRecords()) {
            o.setItems(orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, o.getId())));
            User user = userMapper.selectById(o.getUserId());
            if (user != null) o.setUsername(user.getNickname());
        }
        return Result.success(p);
    }

    @Log("修改订单状态")
    @PutMapping("/order/status")
    public Result<?> updateOrderStatus(@RequestBody Order order) {
        Order o = new Order();
        o.setId(order.getId());
        o.setStatus(order.getStatus());
        orderMapper.updateById(o);
        return Result.success();
    }

    // ============ 留言管理 ============
    @GetMapping("/message/list")
    public Result<?> messageList(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) Integer status) {
        Page<MessageBoard> p = new Page<>(page, size);
        LambdaQueryWrapper<MessageBoard> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(MessageBoard::getStatus, status);
        }
        wrapper.orderByDesc(MessageBoard::getCreateTime);
        messageMapper.selectPage(p, wrapper);
        return Result.success(p);
    }

    @Log("审核留言")
    @PutMapping("/message/audit")
    public Result<?> auditMessage(@RequestBody MessageBoard message) {
        MessageBoard m = new MessageBoard();
        m.setId(message.getId());
        m.setStatus(message.getStatus());
        m.setReply(message.getReply());
        messageMapper.updateById(m);
        return Result.success();
    }

    @Log("删除留言")
    @DeleteMapping("/message/{id}")
    public Result<?> deleteMessage(@PathVariable Long id) {
        messageMapper.deleteById(id);
        return Result.success();
    }

    // ============ 轮播图管理 ============
    @GetMapping("/banner/list")
    public Result<?> bannerList() {
        return Result.success(bannerMapper.selectList(
                new LambdaQueryWrapper<Banner>().orderByAsc(Banner::getSortOrder)));
    }

    @Log("新增/编辑轮播图")
    @PostMapping("/banner")
    public Result<?> saveBanner(@RequestBody Banner banner) {
        if (banner.getId() == null) {
            bannerMapper.insert(banner);
        } else {
            bannerMapper.updateById(banner);
        }
        return Result.success();
    }

    @Log("删除轮播图")
    @DeleteMapping("/banner/{id}")
    public Result<?> deleteBanner(@PathVariable Long id) {
        bannerMapper.deleteById(id);
        return Result.success();
    }

    // ============ 操作日志 ============
    @GetMapping("/log/list")
    public Result<?> logList(@RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "20") int size) {
        Page<OperationLog> p = new Page<>(page, size);
        logMapper.selectPage(p, new LambdaQueryWrapper<OperationLog>()
                .orderByDesc(OperationLog::getCreateTime));
        return Result.success(p);
    }

    // ============ 全局搜索 ============
    @GetMapping("/search")
    public Result<?> search(@RequestParam String keyword) {
        Map<String, Object> result = new HashMap<>();
        result.put("projects", projectMapper.selectList(
                new LambdaQueryWrapper<HeritageProject>()
                        .like(HeritageProject::getTitle, keyword).last("LIMIT 5")));
        result.put("inheritors", inheritorMapper.selectList(
                new LambdaQueryWrapper<Inheritor>()
                        .like(Inheritor::getName, keyword).last("LIMIT 5")));
        result.put("products", productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                        .like(Product::getName, keyword).last("LIMIT 5")));
        return Result.success(result);
    }
}
