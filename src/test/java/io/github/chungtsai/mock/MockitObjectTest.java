package io.github.chungtsai.mock;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class MockitObjectTest {

	MockitObject mockitObject = new MockitObject();

	public MockitObjectTest() {
		mockitObject.registerName("name", () -> "tome");
		mockitObject.exclude("appleVo");
	}

	@Test
	void test_mockObject() {

		AppleVoDTO random = mockitObject.random(new AppleVoDTO(), i -> {
			i.setName4("name4");// 額外待其他自訂值
		});

		log.info("{}", random);

		assertThat(random.getName()).isEqualTo("tome");
		assertThat(random.getName1()).isNotEmpty();
		assertThat(random.getAppleVo()).isNull();
		assertThat(random.getShort1()).isGreaterThan((short) 0);
		assertThat(random.getShort2()).isGreaterThan((short) 0);

	}

	@Test
	void test_mockObjectList() {
		List<AppleVoDTO> list = new ArrayList<>();
		mockitObject.randomList(list, () -> new AppleVoDTO(), i -> {//mock清單
			i.setName4("list");
		}, 5);
		assertThat(list.size()).isEqualTo(5);
		assertThat(list.get(0).getName4()).isEqualTo("list");
	}
	
	@Test
	void test_randomList() {
		List<AppleVoDTO> randomList = mockitObject.randomList(AppleVoDTO::new, (i)->{}, 10);
		assertThat(randomList.size()).isEqualTo(10);
	}

}
