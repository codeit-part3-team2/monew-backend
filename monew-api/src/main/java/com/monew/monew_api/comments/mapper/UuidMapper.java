package com.monew.monew_api.comments.mapper;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

public final class UuidMapper {

	private UuidMapper() {
		// Utility class
	}

	/**
	 * UUID(String) → Long(BigInt)
	 *
	 * 프론트에서 전달된 UUID를 내부 DB에서 사용하는 Long으로 변환
	 */
	public static Long toLong(String uuidString) {
		if (uuidString == null || uuidString.isBlank()) {
			return null;
		}

		UUID uuid = UUID.fromString(uuidString);
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();

		// msb + lsb를 합쳐서 Long으로 변환 (충돌 최소화)
		// 64bit에 맞추기 위해 XOR 사용
		return Math.abs(msb ^ lsb);
	}

	/**
	 * Long(BigInt) → UUID(String)
	 *
	 * DB ID(Long)를 프론트로 보낼 때 UUID 형태 문자열로 변환
	 */
	public static String toUuid(Long id) {
		if (id == null) {
			return null;
		}

		// 단방향 매핑이므로 실제 UUID가 아니라 가상 UUID 생성
		UUID fakeUuid = new UUID(id, 0L);
		return fakeUuid.toString();
	}

	/**
	 * Long(BigInt) → Base64 짧은 토큰 (선택적 사용)
	 *
	 * 프론트에서 짧은 키로 쓸 수 있음 (선택 사항)
	 */
	public static String toBase64(Long id) {
		if (id == null) return null;
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.putLong(id);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.array());
	}

	/**
	 * Base64 → Long(BigInt)
	 */
	public static Long fromBase64(String encoded) {
		if (encoded == null || encoded.isBlank()) return null;
		byte[] bytes = Base64.getUrlDecoder().decode(encoded);
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		return buffer.getLong();
	}
}
