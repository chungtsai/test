package io.github.chungtsai.mock;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class MockitObjectTest {

	@Test
	void test() {

		MockitObject mockitObject = new MockitObject() ;
		mockitObject.registerName("name", () -> "tome");
		mockitObject.exclude("appleVo");

		AppleVoDTO random = mockitObject.random(new AppleVoDTO(), i -> {
			i.setName4("name4");
		});

		log.info("{}", random);

		assertThat(random.getName()).isEqualTo("tome");
		assertThat(random.getName1()).isNotEmpty();
		assertThat(random.getAppleVo()).isNull();
		assertThat(random.getShort1()).isGreaterThan((short) 0);
		assertThat(random.getShort2()).isGreaterThan((short) 0);

		List<AppleVoDTO> list = new ArrayList<>();
		mockitObject.randomList(list, () -> new AppleVoDTO(), i -> {
			i.setName4("list");
		}, 5);
		assertThat(list.size()).isEqualTo(5);
		assertThat(list.get(0).getName4()).isEqualTo("list");
	}

}
