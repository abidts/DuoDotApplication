# Memory Timeline — Design Spec
Date: 2026-06-04

## Goal

Align the Memory Timeline backend with the structure defined in the Whimsical diagram. All features are already implemented; the only gap is that `CommentController` has its `@RestController` annotation commented out and uses a mismatched URL prefix.

## API Surface

| Operation | Endpoint | Method | Notes |
|---|---|---|---|
| Get memories (paginated) | `/memories?page=0&size=10` | GET | Desc order by memoryDate |
| Get total count | `/memories/count` | GET | Separate endpoint (kept as-is) |
| Get single memory | `/memories/{memoryId}` | GET | Includes comments, mediaFiles |
| Create memory | `/memories` | POST | multipart/form-data, files optional |
| Edit memory | `/memories/{memoryId}` | PUT | JSON: memoryDate, description, location |
| Delete memory | `/memories/{memoryId}` | DELETE | |
| Add comment | `/memories/{memoryId}/comments` | POST | Param: description |
| Edit comment | `/memories/{memoryId}/comments/{commentId}` | PUT | Param: description |
| Delete comment | `/memories/{memoryId}/comments/{commentId}` | DELETE | |

## Response Fields

**Memory (in list and single):**
- `id`, `creatorName`, `creatorId`, `memoryDate`, `description`, `location`
- `mediaFiles[]` — `id`, `fileUrl`, `fileName`, `fileType`, `fileSize`, `uploadedAt`
- `comments[]` — `id`, `commenterName`, `commenterId`, `description`, `createdAt`, `updatedAt`
- `lastUpdatedByName`, `createdAt`, `updatedAt`

**"Photo Description"** in the Whimsical diagram = the `description` field on Memory (not a per-photo caption).

## Changes Required

### File: `CommentController.java`

Two line changes only:

1. Restore `@RestController` annotation (currently commented out as `//@RestController`)
2. Change `@RequestMapping` path from `/api/v1/memories/{memoryId}/comments` to `/memories/{memoryId}/comments` to match `MemoryController`

No entity changes, no new DTOs, no database migrations, no service changes.

## Out of Scope

- Per-photo captions (Photo Description confirmed = Memory.description)
- Combining list + count into one response (separate endpoints kept)
- Any new fields on Memory or MediaFile
