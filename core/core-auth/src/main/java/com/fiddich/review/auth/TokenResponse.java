package com.fiddich.review.auth;

public record TokenResponse(String accessToken, String refreshToken) {
}
