# DuoDot API Documentation

**Base URL:** `http://localhost:8080/api/v1`

**Authentication:** JWT Bearer Token required for most endpoints (except register/login)

---

## Authentication Controller

### POST /auth/register
Register a new user account.

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
  }
}
```

---

### POST /auth/login
Login to get JWT tokens.

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
  }
}
```

---

## User Controller

All endpoints require: `Authorization: Bearer {token}`

### GET /users/profile
Get current user's profile.

**Response:**
```json
{
  "success": true,
  "message": "Profile retrieved successfully",
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "profilePictureUrl": "https://..."
  }
}
```

---

### PUT /users/profile
Update user profile.

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response:** Updated user profile

---

### POST /users/profile/picture
Upload profile picture.

**Content-Type:** `multipart/form-data`

**Form Data:**
- `file`: Image file (jpg, png)

**Response:** Updated user profile with new picture URL

---

### GET /users/search?username={username}
Search for a user by username.

**Query Param:** `username` - The username to search

**Response:** User profile if found

---

### DELETE /users/account
Delete current user account permanently.

**Response:**
```json
{
  "success": true,
  "message": "Account deleted successfully"
}
```

---

## Pair Controller

All endpoints require: `Authorization: Bearer {token}`

### POST /pairs/request?username={username}
Send a pair request to another user.

**Query Param:** `username` - Target user's username

**Response:**
```json
{
  "success": true,
  "message": "Pair request sent successfully",
  "data": {
    "id": 1,
    "requesterUsername": "john_doe",
    "receiverUsername": "jane_doe",
    "status": "PENDING"
  }
}
```

---

### GET /pairs/requests/pending
Get all pending pair requests received by current user.

**Response:** List of pair requests

---

### PUT /pairs/requests/{requestId}/accept
Accept a pair request.

**Path Param:** `requestId` - ID of the pair request

**Response:** Accepted pair request

---

### PUT /pairs/requests/{requestId}/reject
Reject a pair request.

**Path Param:** `requestId` - ID of the pair request

**Response:** Rejected pair request

---

### GET /pairs/partner
Get your paired partner's profile (requires active pair).

**Response:** User profile of partner

---

### DELETE /pairs/unpair
Remove the current pair relationship.

**Response:**
```json
{
  "success": true,
  "message": "Unpaired successfully"
}
```

---

## Memory Controller

All endpoints require: `Authorization: Bearer {token}` and active pair

### POST /memories
Create a new memory with optional media files.

**Content-Type:** `multipart/form-data`

**Form Data:**
- `title` (text): Memory title
- `description` (text): Memory description
- `files` (file, optional): Photos/videos to upload (multiple allowed)

**Response:**
```json
{
  "success": true,
  "message": "Memory created successfully",
  "data": {
    "id": 1,
    "title": "Our First Date",
    "description": "Had a wonderful dinner",
    "mediaFiles": [...]
  }
}
```

---

### GET /memories?page={page}&size={size}
Get paginated memories shared with your pair.

**Query Params:**
- `page` (default: 0): Page number
- `size` (default: 10): Items per page

**Response:** Paginated list of memories

---

### GET /memories/{memoryId}
Get a specific memory by ID.

**Path Param:** `memoryId` - Memory ID

**Response:** Memory details with media and comments

---

### PUT /memories/{memoryId}
Update a memory.

**Path Param:** `memoryId` - Memory ID

**Request Body:**
```json
{
  "title": "Updated Title",
  "description": "Updated description"
}
```

**Response:** Updated memory

---

### POST /memories/{memoryId}/media
Add more media files to an existing memory.

**Path Param:** `memoryId` - Memory ID

**Content-Type:** `multipart/form-data`

**Form Data:**
- `files`: Files to upload (multiple allowed)

**Response:** Updated memory with new media

---

### DELETE /memories/{memoryId}
Delete a memory.

**Path Param:** `memoryId` - Memory ID

**Response:**
```json
{
  "success": true,
  "message": "Memory deleted successfully"
}
```

---

### GET /memories/count
Get total number of memories.

**Response:**
```json
{
  "success": true,
  "message": "Total memory count",
  "data": 42
}
```

---

## Comment Controller

All endpoints require: `Authorization: Bearer {token}`

### POST /memories/{memoryId}/comments?description={text}
Add a comment to a memory.

**Path Param:** `memoryId` - Memory ID

**Query Param:** `description` - Comment text

**Response:**
```json
{
  "success": true,
  "message": "Comment added successfully",
  "data": {
    "id": 1,
    "description": "Beautiful memory!",
    "username": "jane_doe",
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

---

### GET /memories/{memoryId}/comments
Get all comments for a memory.

**Path Param:** `memoryId` - Memory ID

**Response:** List of comments

---

### PUT /memories/{memoryId}/comments/{commentId}?description={text}
Update your comment.

**Path Params:**
- `memoryId` - Memory ID
- `commentId` - Comment ID

**Query Param:** `description` - New comment text

**Response:** Updated comment

---

### DELETE /memories/{memoryId}/comments/{commentId}
Delete your comment.

**Path Params:**
- `memoryId` - Memory ID
- `commentId` - Comment ID

**Response:**
```json
{
  "success": true,
  "message": "Comment deleted successfully"
}
```

---

## Response Format

All API responses follow this structure:

### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

---

## HTTP Status Codes

| Code | Meaning |
|------|---------|
| 200 | OK - Success |
| 201 | Created - Resource created |
| 400 | Bad Request - Invalid input |
| 401 | Unauthorized - Missing/invalid token |
| 403 | Forbidden - No permission |
| 404 | Not Found - Resource doesn't exist |
| 409 | Conflict - Resource already exists |
| 500 | Server Error |
