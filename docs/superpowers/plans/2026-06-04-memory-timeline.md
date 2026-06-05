# Memory Timeline Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Re-enable the Comment endpoints so the Memory Timeline matches the Whimsical diagram structure exactly.

**Architecture:** `CommentController` already contains all four comment operations (add, get, edit, delete) with correct service wiring. It was accidentally disabled by commenting out `@RestController`. The fix is two lines: restore the annotation and align the request mapping path with `MemoryController`.

**Tech Stack:** Spring Boot 3.2, Jakarta EE, Lombok

---

### Task 1: Fix CommentController

**Files:**
- Modify: `src/main/java/com/duodot/controller/CommentController.java:8-9`

- [ ] **Step 1: Restore `@RestController` and fix path**

Open `src/main/java/com/duodot/controller/CommentController.java` and apply these two changes:

Line 8 — change:
```java
//@RestController
```
to:
```java
@RestController
```

Line 9 — change:
```java
@RequestMapping("/api/v1/memories/{memoryId}/comments")
```
to:
```java
@RequestMapping("/memories/{memoryId}/comments")
```

The top of the file should now look like:
```java
@RestController
@RequestMapping("/memories/{memoryId}/comments")
@RequiredArgsConstructor
public class CommentController {
```

- [ ] **Step 2: Build the project to confirm no compilation errors**

```bash
./mvnw compile -q
```

Expected: BUILD SUCCESS with no output. If errors appear, re-check that the annotation is on its own line with no leading `//`.

- [ ] **Step 3: Start the application and verify endpoints are registered**

```bash
./mvnw spring-boot:run 2>&1 | grep -E "Mapped|Started|ERROR" | head -30
```

Expected output includes lines like:
```
Mapped "{[/memories/{memoryId}/comments],methods=[POST]}"
Mapped "{[/memories/{memoryId}/comments],methods=[GET]}"
Mapped "{[/memories/{memoryId}/comments/{commentId}],methods=[PUT]}"
Mapped "{[/memories/{memoryId}/comments/{commentId}],methods=[DELETE]}"
```
(Exact format varies by Spring Boot version — look for `/memories/{memoryId}/comments` in the output.)

- [ ] **Step 4: Smoke-test the add-comment endpoint**

With the app running and a valid JWT token, call:
```bash
curl -s -o /dev/null -w "%{http_code}" \
  -X POST "http://localhost:8080/memories/1/comments" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d "description=test comment"
```

Expected: `200` or `404` (if memory id 1 doesn't exist) — **not** `404 No handler found`, which would mean the endpoint is still unregistered.

- [ ] **Step 5: Stop the app and commit**

```bash
git add src/main/java/com/duodot/controller/CommentController.java
git commit -m "fix: re-enable CommentController and align path with MemoryController"
```
