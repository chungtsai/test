package io.github.chungtsai.mock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import io.github.chungtsai.TestBusinessException;

/**
 * 產生隨機資料
 * 
 * @author 6407
 *
 */
public class MockitObject {

	private Map<Class<?>, Supplier<?>> map = Maps.newHashMap();
	private Map<String, Supplier<?>> customer = Maps.newHashMap();
	private Set<String> nameSet = Sets.newHashSet();

	/**
	 * 註冊轉換器
	 * 
	 * @param pClass
	 * @param supplier
	 */
	public void register(Class<?> pClass, Supplier<?> supplier) {
		this.map.put(pClass, supplier);
	}

	public void exclude(String name) {
		this.nameSet.add(name);
	}

	/**
	 * 註冊轉換器
	 * 
	 * @param name
	 * @param supplier
	 */
	public void registerName(String name, Supplier<?> supplier) {
		this.customer.put(name, supplier);
	}

	public MockitObject() {
		this.register(Integer.class, () -> new Random().nextInt());
		this.register(int.class, () -> new Random().nextInt());

		this.register(short.class, () -> (short) new Random().nextInt(Short.MAX_VALUE));
		this.register(Short.class, () -> (short) new Random().nextInt(Short.MAX_VALUE));

		this.register(boolean.class, () -> new Random().nextBoolean());
		this.register(Boolean.class, () -> new Random().nextBoolean());

		this.register(String.class, () -> new Random().nextInt() + "");

		this.register(Double.class, () -> new Random().nextDouble());
		this.register(double.class, () -> new Random().nextDouble());
		this.register(Float.class, () -> new Random().nextFloat());
		this.register(float.class, () -> new Random().nextFloat());

		this.register(long.class, () -> new Random().nextLong());
		this.register(Long.class, () -> new Random().nextLong());

		this.register(BigDecimal.class, () -> BigDecimal.valueOf(new Random().nextLong()));
	}

	public <T> T random(T t, Consumer<T> consumer) {
		Method[] methods = t.getClass().getMethods();
		for (Method method : methods) {
			boolean startsWith = method.getName().startsWith("set");
			if (startsWith) {
				Parameter[] parameters = method.getParameters();
				if (parameters.length == 1) {
					Class<?> type = parameters[0].getType();
					try {
						String uncapitalize = StringUtils.uncapitalize(method.getName().replace("set", ""));
						if (this.nameSet.contains(uncapitalize)) {
							continue;
						}
						if (customer.containsKey(uncapitalize)) {
							method.invoke(t, customer.get(uncapitalize).get());
						} else {
							if (!map.containsKey(type)) {
								throw new TestBusinessException("class {0}無對應隨機規則，請註冊規則", type);
							}
							method.invoke(t, map.get(type).get());

						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new TestBusinessException("class {0}資料異常", type, e);
					}
				}
			}
		}
		if (consumer != null) {
			consumer.accept(t);
		}
		return t;

	}

	public <T> T random(T t) {
		return this.random(t, t1 -> {
			//
		});
	}

	public <T> List<T> randomList(Supplier<T> supplier, Consumer<T> consumer, int appendSize) {
		List<T> list = Lists.newArrayList();
		this.randomList(list, supplier, consumer, appendSize);
		return list;
	}

	public <T> List<T> randomList(List<T> list, Supplier<T> supplier) {
		this.randomList(list, supplier, 1);
		return list;
	}

	public <T> List<T> randomList(List<T> list, Supplier<T> supplier, Consumer<T> consumer, int appendSize) {
		if (appendSize <= 0) {
			throw new TestBusinessException("size is bigger than 0");
		}
		for (int i = 0; i < appendSize; i++) {
			T object = supplier.get();
			list.add(this.random(object, consumer));
		}
		return list;
	}

	public <T> List<T> randomList(List<T> list, Supplier<T> supplier, int size) {
		return this.randomList(list, supplier, null, size);
	}
}
