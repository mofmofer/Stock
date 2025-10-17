package com.example.stock.web;

import com.example.stock.model.AccessLog;
import com.example.stock.service.AccessLogService;
import com.example.stock.web.dto.AccessLogRequest;
import com.example.stock.web.dto.AccessLogView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * アクセスログ関連の REST API を提供します。
 */
@RestController
@RequestMapping("/api/access-logs")
@Validated
public class AccessLogController {

    private static final String DEFAULT_PATH = "/";

    private final AccessLogService accessLogService;

    public AccessLogController(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    /**
     * フロントエンドから送信されたアクセス情報を保存します。
     *
     * @param requestDto   リクエストボディ
     * @param httpRequest  HTTP リクエスト情報
     * @return 保存されたアクセスログ
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccessLogView record(@Valid @RequestBody AccessLogRequest requestDto, HttpServletRequest httpRequest) {
        String page = requestDto.page().trim();
        String path = Optional.ofNullable(requestDto.path())
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElseGet(() -> Optional.ofNullable(httpRequest.getHeader("Referer"))
                        .filter(value -> !value.isBlank())
                        .orElse(DEFAULT_PATH));

        String ipAddress = extractClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        AccessLog log = new AccessLog(page, path, ipAddress, userAgent);
        return toView(accessLogService.save(log));
    }

    /**
     * 直近のアクセスログを取得します。
     *
     * @param page  ページ名
     * @param limit 上限件数
     * @return アクセスログ一覧
     */
    @GetMapping
    public List<AccessLogView> list(
            @RequestParam(name = "page", required = false) String page,
            @RequestParam(name = "limit", defaultValue = "50")
            @Min(value = 1, message = "limit は 1 以上にしてください")
            @Max(value = 500, message = "limit は 500 以下にしてください") int limit) {
        return accessLogService.getRecentLogs(page, limit).stream()
                .map(this::toView)
                .toList();
    }

    /**
     * 記録済みのページ名一覧を取得します。
     *
     * @return ページ名一覧
     */
    @GetMapping("/pages")
    public List<String> pages() {
        return accessLogService.getRegisteredPages();
    }

    private AccessLogView toView(AccessLog log) {
        return new AccessLogView(log.getId(), log.getPage(), log.getPath(), log.getIpAddress(),
                log.getUserAgent(), log.getAccessedAt());
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return Optional.ofNullable(request.getRemoteAddr()).orElse("unknown");
    }
}

