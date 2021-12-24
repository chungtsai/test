package io.github.chungtsai.mock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class MockitObjectTest {

	@Test
	void test() {
		AppleVoDTO appleVoDTO = new AppleVoDTO();
		MockitObject mockitObject = new MockitObject();
		mockitObject.registerName("name", () -> "tome");
		mockitObject.exclude("appleVo");
		AppleVoDTO random = (AppleVoDTO) mockitObject.random(appleVoDTO);
		log.info("{}", random);

		assertThat(random.getName()).isEqualTo("tome");
		assertThat(random.getName1()).isNotEmpty();
		assertThat(random.getAppleVo()).isNull();
	}

}
