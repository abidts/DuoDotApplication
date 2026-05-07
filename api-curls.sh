#!/bin/bash

# DuoDot API - cURL Commands for Postman
# Base URL
BASE_URL="http://localhost:8080/api/v1"

# =============================================================================
# AUTHENTICATION
# =============================================================================

# Register a new user
curl -X POST "${BASE_URL}/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'

# Login
curl -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'

# =============================================================================
# USER PROFILE (Requires Authentication - Add Authorization header)
# =============================================================================

# Get user profile
curl -X GET "${BASE_URL}/users/profile" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Update user profile
curl -X PUT "${BASE_URL}/users/profile" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }'

# Upload profile picture
curl -X POST "${BASE_URL}/users/profile/picture" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@/path/to/profile.jpg"

# Search user by username
curl -X GET "${BASE_URL}/users/search?username=jane_doe" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Delete account
curl -X DELETE "${BASE_URL}/users/account" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# =============================================================================
# PAIR MANAGEMENT (Requires Authentication)
# =============================================================================

# Send pair request to another user
curl -X POST "${BASE_URL}/pairs/request?username=jane_doe" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get pending pair requests
curl -X GET "${BASE_URL}/pairs/requests/pending" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Accept pair request
curl -X PUT "${BASE_URL}/pairs/requests/1/accept" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Reject pair request
curl -X PUT "${BASE_URL}/pairs/requests/1/reject" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get paired user (partner)
curl -X GET "${BASE_URL}/pairs/partner" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Unpair (delete pair)
curl -X DELETE "${BASE_URL}/pairs/unpair" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# =============================================================================
# MEMORIES (Requires Authentication + Must be paired)
# =============================================================================

# Create a new memory with media files
curl -X POST "${BASE_URL}/memories" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "title=Our First Date" \
  -F "description=Had a wonderful dinner at the beach" \
  -F "files=@/path/to/photo1.jpg" \
  -F "files=@/path/to/photo2.jpg"

# Get all memories (paginated)
curl -X GET "${BASE_URL}/memories?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get specific memory by ID
curl -X GET "${BASE_URL}/memories/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Update memory
curl -X PUT "${BASE_URL}/memories/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Title",
    "description": "Updated description"
  }'

# Add media files to existing memory
curl -X POST "${BASE_URL}/memories/1/media" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "files=@/path/to/new_photo.jpg"

# Delete memory
curl -X DELETE "${BASE_URL}/memories/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get total memory count
curl -X GET "${BASE_URL}/memories/count" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# =============================================================================
# COMMENTS (Requires Authentication)
# =============================================================================

# Add comment to memory
curl -X POST "${BASE_URL}/memories/1/comments?description=This is a beautiful memory!" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get all comments for a memory
curl -X GET "${BASE_URL}/memories/1/comments" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Update comment
curl -X PUT "${BASE_URL}/memories/1/comments/1?description=Updated comment text" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Delete comment
curl -X DELETE "${BASE_URL}/memories/1/comments/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
