package com.aviation.mro.config;


import com.aviation.mro.modules.auth.model.Permission;
import com.aviation.mro.modules.auth.model.Role;
import com.aviation.mro.modules.auth.model.User;
import com.aviation.mro.modules.auth.repository.PermissionRepository;
import com.aviation.mro.modules.auth.repository.RoleRepository;
import com.aviation.mro.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("شروع اولیه‌سازی داده‌های سیستم...");

        try {
            initPermissions();
            initRoles();
            initUsers();

            log.info("اولیه‌سازی داده‌ها با موفقیت انجام شد.");
            log.info("کاربر پیش‌فرض: admin / Admin@1234");
        } catch (Exception e) {
            log.error("خطا در اولیه‌سازی داده‌ها: {}", e.getMessage(), e);
        }
    }

    private void initPermissions() {
        log.info("ایجاد دسترسی‌های سیستم...");

        // لیست تمام Permissionهای سیستم
        List<PermissionData> permissionDataList = Arrays.asList(
                // مدیریت سیستم
                new PermissionData("MANAGE_SYSTEM", "System", "مدیریت کامل سیستم"),
                new PermissionData("MANAGE_ROLES", "System", "مدیریت نقش‌ها"),
                new PermissionData("MANAGE_USERS", "System", "مدیریت کاربران"),
                new PermissionData("MANAGE_PERMISSIONS", "System", "مدیریت دسترسی‌ها"),
                new PermissionData("VIEW_AUDIT_LOGS", "System", "مشاهده لاگ‌های سیستم"),

                // ماژول قطعات (Parts)
                new PermissionData("VIEW_PARTS", "Parts", "مشاهده قطعات"),
                new PermissionData("CREATE_PARTS", "Parts", "ایجاد قطعات جدید"),
                new PermissionData("EDIT_PARTS", "Parts", "ویرایش قطعات"),
                new PermissionData("DELETE_PARTS", "Parts", "حذف قطعات"),
                new PermissionData("APPROVE_PARTS", "Parts", "تأیید قطعات"),
                new PermissionData("REJECT_PARTS", "Parts", "رد قطعات"),
                new PermissionData("EXPORT_PARTS", "Parts", "خروجی گرفتن از قطعات"),
                new PermissionData("IMPORT_PARTS", "Parts", "ورود اطلاعات قطعات"),

                // ماژول انبار (Warehouse/Inventory)
                new PermissionData("VIEW_INVENTORY", "Warehouse", "مشاهده موجودی انبار"),
                new PermissionData("MANAGE_INVENTORY", "Warehouse", "مدیریت موجودی انبار"),
                new PermissionData("UPDATE_STOCK", "Warehouse", "به‌روزرسانی موجودی"),
                new PermissionData("VIEW_STOCK_HISTORY", "Warehouse", "مشاهده تاریخچه موجودی"),
                new PermissionData("MANAGE_WAREHOUSES", "Warehouse", "مدیریت انبارها"),
                new PermissionData("PROCUREMENT_REQUEST", "Warehouse", "درخواست خرید"),
                new PermissionData("APPROVE_PROCUREMENT", "Warehouse", "تأیید درخواست خرید"),

                // ماژول فروش (Sales)
                new PermissionData("VIEW_SALES", "Sales", "مشاهده فروش"),
                new PermissionData("MANAGE_SALES", "Sales", "مدیریت فروش"),
                new PermissionData("CREATE_ORDER", "Sales", "ایجاد سفارش"),
                new PermissionData("EDIT_ORDER", "Sales", "ویرایش سفارش"),
                new PermissionData("CANCEL_ORDER", "Sales", "لغو سفارش"),
                new PermissionData("VIEW_CUSTOMERS", "Sales", "مشاهده مشتریان"),
                new PermissionData("MANAGE_CUSTOMERS", "Sales", "مدیریت مشتریان"),
                new PermissionData("CREATE_QUOTATION", "Sales", "ایجاد پیش‌فاکتور"),
                new PermissionData("APPROVE_QUOTATION", "Sales", "تأیید پیش‌فاکتور"),
                new PermissionData("CREATE_INVOICE", "Sales", "ایجاد فاکتور"),

                // ماژول حسابداری (Accounting)
                new PermissionData("VIEW_ACCOUNTING", "Accounting", "مشاهده حسابداری"),
                new PermissionData("MANAGE_ACCOUNTING", "Accounting", "مدیریت حسابداری"),
                new PermissionData("VIEW_ACCOUNTS", "Accounting", "مشاهده حساب‌ها"),
                new PermissionData("MANAGE_ACCOUNTS", "Accounting", "مدیریت حساب‌ها"),
                new PermissionData("CREATE_TRANSACTION", "Accounting", "ایجاد تراکنش"),
                new PermissionData("APPROVE_TRANSACTION", "Accounting", "تأیید تراکنش"),
                new PermissionData("VIEW_FINANCIAL_REPORTS", "Accounting", "مشاهده گزارشات مالی"),
                new PermissionData("EXPORT_FINANCIAL_REPORTS", "Accounting", "خروجی گزارشات مالی"),

                // ماژول کیفیت (Quality)
                new PermissionData("VIEW_QUALITY", "Quality", "مشاهده کیفیت"),
                new PermissionData("MANAGE_QUALITY", "Quality", "مدیریت کیفیت"),
                new PermissionData("CREATE_INSPECTION", "Quality", "ایجاد بازرسی"),
                new PermissionData("APPROVE_INSPECTION", "Quality", "تأیید بازرسی"),
                new PermissionData("REJECT_INSPECTION", "Quality", "رد بازرسی"),
                new PermissionData("MANAGE_QUALITY_PLANS", "Quality", "مدیریت طرح‌های کیفیت"),
                new PermissionData("VIEW_NON_CONFORMANCE", "Quality", "مشاهده عدم انطباق"),
                new PermissionData("MANAGE_NON_CONFORMANCE", "Quality", "مدیریت عدم انطباق"),

                // ماژول تعمیرات (Repair/Maintenance)
                new PermissionData("VIEW_REPAIR", "Repair", "مشاهده تعمیرات"),
                new PermissionData("MANAGE_REPAIR", "Repair", "مدیریت تعمیرات"),
                new PermissionData("CREATE_WORK_ORDER", "Repair", "ایجاد دستور کار"),
                new PermissionData("ASSIGN_WORK_ORDER", "Repair", "تخصیص دستور کار"),
                new PermissionData("COMPLETE_WORK_ORDER", "Repair", "اتمام دستور کار"),
                new PermissionData("VIEW_REPAIR_HISTORY", "Repair", "مشاهده تاریخچه تعمیرات"),
                new PermissionData("MANAGE_REPAIR_SCHEDULE", "Repair", "مدیریت برنامه تعمیرات"),

                // ماژول گزارشات (Reports)
                new PermissionData("VIEW_REPORTS", "Reports", "مشاهده گزارشات"),
                new PermissionData("EXPORT_REPORTS", "Reports", "خروجی گرفتن از گزارشات"),
                new PermissionData("VIEW_DASHBOARD", "Reports", "مشاهده داشبورد"),
                new PermissionData("CUSTOM_REPORT", "Reports", "ایجاد گزارش سفارشی"),

                // ماژول اطلاع‌رسانی (Notifications)
                new PermissionData("VIEW_NOTIFICATIONS", "Notifications", "مشاهده اطلاعیه‌ها"),
                new PermissionData("MANAGE_NOTIFICATIONS", "Notifications", "مدیریت اطلاعیه‌ها"),
                new PermissionData("SEND_NOTIFICATIONS", "Notifications", "ارسال اطلاعیه"),

                // ماژول تنظیمات (Settings)
                new PermissionData("VIEW_SETTINGS", "Settings", "مشاهده تنظیمات"),
                new PermissionData("MANAGE_SETTINGS", "Settings", "مدیریت تنظیمات"),
                new PermissionData("MANAGE_COMPANY_INFO", "Settings", "مدیریت اطلاعات شرکت"),
                new PermissionData("MANAGE_TAX_SETTINGS", "Settings", "مدیریت تنظیمات مالیاتی")
        );

        int createdCount = 0;
        for (PermissionData data : permissionDataList) {
            if (!permissionRepository.existsByName(data.name)) {
                Permission permission = new Permission();
                permission.setName(data.name);
                permission.setModule(data.module);
                permission.setDescription(data.description);
                permission.setCreatedAt(LocalDateTime.now());
                permissionRepository.save(permission);
                createdCount++;
                log.debug("دسترسی ایجاد شد: {}", data.name);
            }
        }

        log.info("تعداد {} دسترسی ایجاد/بررسی شد.", createdCount);
    }

    private void initRoles() {
        log.info("ایجاد نقش‌های پیش‌فرض سیستم...");

        // 1. نقش مدیر ارشد (Super Admin) - تمام دسترسی‌ها
        createSuperAdminRole();

        // 2. نقش مدیر سیستم (System Admin) - دسترسی‌های مدیریتی
        createSystemAdminRole();

        // 3. نقش مدیر فنی (Technical Manager)
        createTechnicalManagerRole();

        // 4. نقش مدیر فروش (Sales Manager)
        createSalesManagerRole();

        // 5. نقش مدیر مالی (Accounting Manager)
        createAccountingManagerRole();

        // 6. نقش مدیر کیفیت (Quality Manager)
        createQualityManagerRole();

        // 7. نقش مدیر انبار (Warehouse Manager)
        createWarehouseManagerRole();

        // 8. نقش تکنسین (Technician)
        createTechnicianRole();

        // 9. نقش بازرس کیفیت (Quality Inspector)
        createQualityInspectorRole();

        // 10. نقش کارمند فروش (Sales Representative)
        createSalesRepresentativeRole();

        // 11. نقش حسابدار (Accountant)
        createAccountantRole();

        // 12. نقش انباردار (Storekeeper)
        createStorekeeperRole();

        // 13. نقش مشاهده‌گر (Viewer) - فقط دسترسی مشاهده
        createViewerRole();

        log.info("نقش‌های پیش‌فرض ایجاد شدند.");
    }

    private void createSuperAdminRole() {
        if (!roleRepository.existsByName("SUPER_ADMIN")) {
            Role role = new Role();
            role.setName("SUPER_ADMIN");
            role.setDisplayName("مدیر ارشد سیستم");
            role.setDescription("دسترسی کامل به تمامی بخش‌های سیستم. این نقش قابل حذف نیست.");
            role.setSystem(true);
            role.setCreatedAt(LocalDateTime.now());

            // دادن تمام دسترسی‌ها
            List<Permission> allPermissions = permissionRepository.findAll();
            role.setPermissions(new HashSet<>(allPermissions));

            roleRepository.save(role);
            log.info("نقش SUPER_ADMIN با {} دسترسی ایجاد شد.", allPermissions.size());
        }
    }

    private void createSystemAdminRole() {
        if (!roleRepository.existsByName("SYSTEM_ADMIN")) {
            Role role = new Role();
            role.setName("SYSTEM_ADMIN");
            role.setDisplayName("مدیر سیستم");
            role.setDescription("مدیریت کاربران، نقش‌ها و تنظیمات سیستم");
            role.setSystem(true);
            role.setCreatedAt(LocalDateTime.now());

            Set<String> permissionNames = new HashSet<>(Arrays.asList(
                    "MANAGE_SYSTEM", "MANAGE_ROLES", "MANAGE_USERS", "MANAGE_PERMISSIONS",
                    "VIEW_AUDIT_LOGS", "VIEW_SETTINGS", "MANAGE_SETTINGS",
                    "MANAGE_COMPANY_INFO", "MANAGE_TAX_SETTINGS"
            ));

            setPermissionsToRole(role, permissionNames);
            roleRepository.save(role);
            log.info("نقش SYSTEM_ADMIN ایجاد شد.");
        }
    }

    private void createTechnicalManagerRole() {
        if (!roleRepository.existsByName("TECHNICAL_MANAGER")) {
            Role role = new Role();
            role.setName("TECHNICAL_MANAGER");
            role.setDisplayName("مدیر فنی");
            role.setDescription("مدیریت بخش فنی، تعمیرات و قطعات");
            role.setSystem(false);
            role.setCreatedAt(LocalDateTime.now());

            Set<String> permissionNames = new HashSet<>(Arrays.asList(
                    "VIEW_PARTS", "CREATE_PARTS", "EDIT_PARTS", "DELETE_PARTS", "APPROVE_PARTS", "REJECT_PARTS",
                    "EXPORT_PARTS", "IMPORT_PARTS", "VIEW_INVENTORY", "MANAGE_INVENTORY", "UPDATE_STOCK",
                    "VIEW_REPAIR", "MANAGE_REPAIR", "CREATE_WORK_ORDER", "ASSIGN_WORK_ORDER",
                    "COMPLETE_WORK_ORDER", "MANAGE_REPAIR_SCHEDULE", "VIEW_REPORTS", "VIEW_DASHBOARD"
            ));

            setPermissionsToRole(role, permissionNames);
            roleRepository.save(role);
            log.info("نقش TECHNICAL_MANAGER ایجاد شد.");
        }
    }

    private void createSalesManagerRole() {
        if (!roleRepository.existsByName("SALES_MANAGER")) {
            Role role = new Role();
            role.setName("SALES_MANAGER");
            role.setDisplayName("مدیر فروش");
            role.setDescription("مدیریت بخش فروش و مشتریان");
            role.setSystem(false);
            role.setCreatedAt(LocalDateTime.now());

            Set<String> permissionNames = new HashSet<>(Arrays.asList(
                    "VIEW_SALES", "MANAGE_SALES", "CREATE_ORDER", "EDIT_ORDER", "CANCEL_ORDER",
                    "VIEW_CUSTOMERS", "MANAGE_CUSTOMERS", "CREATE_QUOTATION", "APPROVE_QUOTATION",
                    "CREATE_INVOICE", "VIEW_PARTS", "VIEW_INVENTORY", "VIEW_REPORTS",
                    "EXPORT_REPORTS", "VIEW_DASHBOARD", "VIEW_ACCOUNTING"
            ));

            setPermissionsToRole(role, permissionNames);
            roleRepository.save(role);
            log.info("نقش SALES_MANAGER ایجاد شد.");
        }
    }

    private void createAccountingManagerRole() {
        if (!roleRepository.existsByName("ACCOUNTING_MANAGER")) {
            Role role = new Role();
            role.setName("ACCOUNTING_MANAGER");
            role.setDisplayName("مدیر مالی");
            role.setDescription("مدیریت بخش حسابداری و مالی");
            role.setSystem(false);
            role.setCreatedAt(LocalDateTime.now());

            Set<String> permissionNames = new HashSet<>(Arrays.asList(
                    "VIEW_ACCOUNTING", "MANAGE_ACCOUNTING", "VIEW_ACCOUNTS", "MANAGE_ACCOUNTS",
                    "CREATE_TRANSACTION", "APPROVE_TRANSACTION", "VIEW_FINANCIAL_REPORTS",
                    "EXPORT_FINANCIAL_REPORTS", "VIEW_SALES", "VIEW_REPORTS", "VIEW_DASHBOARD",
                    "MANAGE_TAX_SETTINGS"
            ));

            setPermissionsToRole(role, permissionNames);
            roleRepository.save(role);
            log.info("نقش ACCOUNTING_MANAGER ایجاد شد.");
        }
    }

    private void createQualityManagerRole() {
        if (!roleRepository.existsByName("QUALITY_MANAGER")) {
            Role role = new Role();
            role.setName("QUALITY_MANAGER");
            role.setDisplayName("مدیر کیفیت");
            role.setDescription("مدیریت بخش کنترل کیفیت");
            role.setSystem(false);
            role.setCreatedAt(LocalDateTime.now());

            Set<String> permissionNames = new HashSet<>(Arrays.asList(
                    "VIEW_QUALITY", "MANAGE_QUALITY", "CREATE_INSPECTION", "APPROVE_INSPECTION",
                    "REJECT_INSPECTION", "MANAGE_QUALITY_PLANS", "VIEW_NON_CONFORMANCE",
                    "MANAGE_NON_CONFORMANCE", "VIEW_PARTS", "APPROVE_PARTS", "REJECT_PARTS",
                    "VIEW_REPAIR", "VIEW_REPORTS", "EXPORT_REPORTS", "VIEW_DASHBOARD"
            ));

            setPermissionsToRole(role, permissionNames);
            roleRepository.save(role);
            log.info("نقش QUALITY_MANAGER ایجاد شد.");
        }
    }

    private void createWarehouseManagerRole() {
        if (!roleRepository.existsByName("WAREHOUSE_MANAGER")) {
            Role role = new Role();
            role.setName("WAREHOUSE_MANAGER");
            role.setDisplayName("مدیر انبار");
            role.setDescription("مدیریت انبار و موجودی");
            role.setSystem(false);
            role.setCreatedAt(LocalDateTime.now());

            Set<String> permissionNames = new HashSet<>(Arrays.asList(
                    "VIEW_INVENTORY", "MANAGE_INVENTORY", "UPDATE_STOCK", "VIEW_STOCK_HISTORY",
                    "MANAGE_WAREHOUSES", "PROCUREMENT_REQUEST", "APPROVE_PROCUREMENT",
                    "VIEW_PARTS", "CREATE_PARTS", "EDIT_PARTS", "VIEW_REPAIR",
                    "VIEW_REPORTS", "VIEW_DASHBOARD"
            ));

            setPermissionsToRole(role, permissionNames);
            roleRepository.save(role);
            log.info("نقش WAREHOUSE_MANAGER ایجاد شد.");
        }
    }

    private void createTechnicianRole() {
        if (!roleRepository.existsByName("TECHNICIAN")) {
            Role role = new Role();
            role.setName("TECHNICIAN");
            role.setDisplayName("تکنسین");
            role.setDescription("انجام عملیات فنی و تعمیرات");
            role.setSystem(false);
            role.setCreatedAt(LocalDateTime.now());

            Set<String> permissionNames = new HashSet<>(Arrays.asList(
                    "VIEW_PARTS", "CREATE_PARTS", "EDIT_PARTS", "VIEW_INVENTORY",
                    "VIEW_REPAIR", "CREATE_WORK_ORDER", "COMPLETE_WORK_ORDER",
                    "VIEW_REPAIR_HISTORY", "VIEW_NOTIFICATIONS"
            ));

            setPermissionsToRole(role, permissionNames);
            roleRepository.save(role);
            log.info("نقش TECHNICIAN ایجاد شد.");
        }
    }

    private void createQualityInspectorRole() {
        if (!roleRepository.existsByName("QUALITY_INSPECTOR")) {
            Role role = new Role();
            role.setName("QUALITY_INSPECTOR");
            role.setDisplayName("بازرس کیفیت");
            role.setDescription("کنترل کیفیت قطعات و محصولات");
            role.setSystem(false);
            role.setCreatedAt(LocalDateTime.now());

            Set<String> permissionNames = new HashSet<>(Arrays.asList(
                    "VIEW_QUALITY", "CREATE_INSPECTION", "APPROVE_INSPECTION", "REJECT_INSPECTION",
                    "VIEW_NON_CONFORMANCE", "VIEW_PARTS", "APPROVE_PARTS", "REJECT_PARTS",
                    "VIEW_REPAIR", "VIEW_NOTIFICATIONS"
            ));

            setPermissionsToRole(role, permissionNames);
            roleRepository.save(role);
            log.info("نقش QUALITY_INSPECTOR ایجاد شد.");
        }
    }

    private void createSalesRepresentativeRole() {
        if (!roleRepository.existsByName("SALES_REPRESENTATIVE")) {
            Role role = new Role();
            role.setName("SALES_REPRESENTATIVE");
            role.setDisplayName("کارمند فروش");
            role.setDescription("فعالیت در بخش فروش و مشتریان");
            role.setSystem(false);
            role.setCreatedAt(LocalDateTime.now());

            Set<String> permissionNames = new HashSet<>(Arrays.asList(
                    "VIEW_SALES", "CREATE_ORDER", "VIEW_CUSTOMERS", "CREATE_QUOTATION",
                    "VIEW_PARTS", "VIEW_INVENTORY", "CREATE_INVOICE", "VIEW_NOTIFICATIONS"
            ));

            setPermissionsToRole(role, permissionNames);
            roleRepository.save(role);
            log.info("نقش SALES_REPRESENTATIVE ایجاد شد.");
        }
    }

    private void createAccountantRole() {
        if (!roleRepository.existsByName("ACCOUNTANT")) {
            Role role = new Role();
            role.setName("ACCOUNTANT");
            role.setDisplayName("حسابدار");
            role.setDescription("انجام امور حسابداری");
            role.setSystem(false);
            role.setCreatedAt(LocalDateTime.now());

            Set<String> permissionNames = new HashSet<>(Arrays.asList(
                    "VIEW_ACCOUNTING", "VIEW_ACCOUNTS", "CREATE_TRANSACTION",
                    "VIEW_FINANCIAL_REPORTS", "VIEW_SALES", "VIEW_NOTIFICATIONS"
            ));

            setPermissionsToRole(role, permissionNames);
            roleRepository.save(role);
            log.info("نقش ACCOUNTANT ایجاد شد.");
        }
    }

    private void createStorekeeperRole() {
        if (!roleRepository.existsByName("STOREKEEPER")) {
            Role role = new Role();
            role.setName("STOREKEEPER");
            role.setDisplayName("انباردار");
            role.setDescription("مدیریت انبار و موجودی");
            role.setSystem(false);
            role.setCreatedAt(LocalDateTime.now());

            Set<String> permissionNames = new HashSet<>(Arrays.asList(
                    "VIEW_INVENTORY", "UPDATE_STOCK", "VIEW_STOCK_HISTORY",
                    "VIEW_PARTS", "PROCUREMENT_REQUEST", "VIEW_NOTIFICATIONS"
            ));

            setPermissionsToRole(role, permissionNames);
            roleRepository.save(role);
            log.info("نقش STOREKEEPER ایجاد شد.");
        }
    }

    private void createViewerRole() {
        if (!roleRepository.existsByName("VIEWER")) {
            Role role = new Role();
            role.setName("VIEWER");
            role.setDisplayName("مشاهده‌گر");
            role.setDescription("فقط دسترسی مشاهده به اطلاعات");
            role.setSystem(false);
            role.setCreatedAt(LocalDateTime.now());

            Set<String> permissionNames = new HashSet<>(Arrays.asList(
                    "VIEW_PARTS", "VIEW_INVENTORY", "VIEW_SALES", "VIEW_ACCOUNTING",
                    "VIEW_QUALITY", "VIEW_REPAIR", "VIEW_REPORTS", "VIEW_DASHBOARD",
                    "VIEW_NOTIFICATIONS"
            ));

            setPermissionsToRole(role, permissionNames);
            roleRepository.save(role);
            log.info("نقش VIEWER ایجاد شد.");
        }
    }

    private void setPermissionsToRole(Role role, Set<String> permissionNames) {
        Set<Permission> permissions = new HashSet<>();
        for (String permissionName : permissionNames) {
            permissionRepository.findByName(permissionName)
                    .ifPresent(permissions::add);
        }
        role.setPermissions(permissions);
    }

    private void initUsers() {
        log.info("ایجاد کاربران پیش‌فرض سیستم...");

        // 1. کاربر مدیر ارشد
        createUserIfNotExists(
                "admin",
                "admin@aviation-mro.com",
                "admin@123", // پسورد قوی
                "مدیر",
                "ارشد سیستم",
                "SUPER_ADMIN"
        );

        // 2. کاربر مدیر فنی
        createUserIfNotExists(
                "tech_manager",
                "tech.manager@aviation-mro.com",
                "tech_manager@123",
                "علی",
                "فنی",
                "TECHNICAL_MANAGER"
        );

        // 3. کاربر تکنسین
        createUserIfNotExists(
                "technician1",
                "technician1@aviation-mro.com",
                "Tech1@1234",
                "رضا",
                "تکنسین",
                "TECHNICIAN"
        );

        // 4. کاربر بازرس کیفیت
        createUserIfNotExists(
                "inspector1",
                "inspector1@aviation-mro.com",
                "Inspector@1234",
                "مریم",
                "بازرس",
                "QUALITY_INSPECTOR"
        );

        // 5. کاربر مدیر فروش
        createUserIfNotExists(
                "sales_manager",
                "sales.manager@aviation-mro.com",
                "Sales@1234",
                "سارا",
                "فروش",
                "SALES_MANAGER"
        );

        // 6. کاربر کارمند فروش
        createUserIfNotExists(
                "sales_rep",
                "sales.rep@aviation-mro.com",
                "SalesRep@1234",
                "حسین",
                "فروشنده",
                "SALES_REPRESENTATIVE"
        );

        // 7. کاربر مدیر مالی
        createUserIfNotExists(
                "accounting_manager",
                "accounting.manager@aviation-mro.com",
                "Accounting@1234",
                "محمد",
                "مالی",
                "ACCOUNTING_MANAGER"
        );

        // 8. کاربر حسابدار
        createUserIfNotExists(
                "accountant1",
                "accountant1@aviation-mro.com",
                "Accountant@1234",
                "فاطمه",
                "حسابدار",
                "ACCOUNTANT"
        );

        // 9. کاربر مدیر انبار
        createUserIfNotExists(
                "warehouse_manager",
                "warehouse.manager@aviation-mro.com",
                "Warehouse@1234",
                "اکبر",
                "انبار",
                "WAREHOUSE_MANAGER"
        );

        // 10. کاربر انباردار
        createUserIfNotExists(
                "storekeeper1",
                "storekeeper1@aviation-mro.com",
                "Storekeeper@1234",
                "زهرا",
                "انباردار",
                "STOREKEEPER"
        );

        // 11. کاربر مدیر کیفیت
        createUserIfNotExists(
                "quality_manager",
                "quality.manager@aviation-mro.com",
                "Quality@1234",
                "مجید",
                "کیفیت",
                "QUALITY_MANAGER"
        );

        // 12. کاربر مشاهده‌گر
        createUserIfNotExists(
                "viewer1",
                "viewer1@aviation-mro.com",
                "Viewer@1234",
                "ناظر",
                "سیستم",
                "VIEWER"
        );

        log.info("کاربران پیش‌فرض ایجاد شدند.");
    }

    private void createUserIfNotExists(String username, String email, String password,
                                       String firstName, String lastName, String roleName) {
        if (userRepository.findByUsername(username).isEmpty()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEnabled(true);
            user.setDeleted(false);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            user.getRoles().add(role);

            userRepository.save(user);
            log.info("کاربر {} با نقش {} ایجاد شد.", username, roleName);
        }
    }

    // کلاس کمکی برای داده‌های Permission
    private static class PermissionData {
        String name;
        String module;
        String description;

        PermissionData(String name, String module, String description) {
            this.name = name;
            this.module = module;
            this.description = description;
        }
    }
}
