package cn.ching.srpg_demo.game.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cn.ching.srpg_demo.game.Resource;
import cn.ching.srpg_demo.game.map.Hexagon;

@SuppressWarnings({"rawtypes","unchecked"})
public class DataAccessLayer {

	public static Pattern arraySeparator = Pattern.compile("[\\s,]+");
	public static int[] parseIntArray(String string) {
		String[] ss = arraySeparator.split(string);
		int[] array = new int[ss.length];
		int i = 0;
		for(String s : ss) {
			array[i++] = Integer.parseInt(s.toString());
		}
		return array;
	}

	public static interface Parser  {
		public Object parse(String s);
	}

	static Map<Class, Parser> parsers = new HashMap<Class, Parser>();
	static {
		Parser parser = new Parser() {
			@Override
			public Object parse(String s) {
				return s;
			}
		};
		parsers.put(String.class, parser);
		parser = new Parser() {
			@Override
			public Object parse(String s) {
				return arraySeparator.split(s);
			}
		};
		parsers.put(String[].class, parser);
		parser = new Parser() {
			@Override
			public Object parse(String s) {
				return Boolean.valueOf(s);
			}
		};
		parsers.put(boolean.class, parser);
		parsers.put(Boolean.class, parser);
		parser = new Parser() {
			@Override
			public Object parse(String s) {
				return Integer.valueOf(s);
			}
		};
		parsers.put(int.class, parser);
		parsers.put(Integer.class, parser);
		parser = new Parser() {
			@Override
			public Object parse(String s) {
				return parseIntArray(s);
			}
		};
		parsers.put(int[].class, parser);
		parser = new Parser() {
			@Override
			public Object parse(String s) {
				BufferedReader reader = new BufferedReader(new StringReader(s));
				String line = null;
				List<int[]> list = new ArrayList<int[]>();
				int span = 0;
				try {
					while((line = reader.readLine()) != null) {
						if(line.trim().length() > 0) {
							int[] a = parseIntArray(line);
							if(a.length > span) span = a.length;
							list.add(a);
						}
					}
				} catch(Exception x) {
					throw new RuntimeException(x);
				}
				return list.toArray(new int[span][list.size()]);
			}
		};
		parsers.put(int[][].class, parser);
		parser = new Parser() {
			@Override
			public Object parse(String s) {
				return Float.valueOf(s);
			}
		};
		parsers.put(float.class, parser);
		parsers.put(Float.class, parser);
		parser = new Parser() {
			@Override
			public Object parse(String s) {
				try {
					return Class.forName(s);
				} catch(ClassNotFoundException x) {
					throw new RuntimeException(x);
				}
			}
		};
		parsers.put(Class.class, parser);
		parser = new Parser() {
			@Override
			public Object parse(String s) {
				int[] args = parseIntArray(s);
				return new Hexagon(args[0], args[1], args[2], args[3], args[4]);
			}
		};
		parsers.put(Hexagon.class, parser);
	}

	static Object guess(String s) {
		String[] a = arraySeparator.split(s);
		if(a.length > 1) {
			try {
				int[] a1 = new int[a.length];
				int i = 0;
				for(String s1 : a) {
					a1[i++] = Integer.parseInt(s1);
				}
				return a1;
			} catch(Exception x) {
				return a;
			}
		} else {
			try {
				return Integer.parseInt(s);
			} catch(Exception x) {
				try {
					return Boolean.parseBoolean(s);
				} catch(Exception x2) {
					return s;
				}
			}
		}
	}

	public class Handler extends DefaultHandler {
		Stack<Object[]> stack = new Stack<Object[]>();
		Object result = null;
		@Override
		public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
			if(stack.isEmpty()) {
				if(result == null) {
					Class type = decideType(localName, attributes);
					result = newObject(type, null);
				}
				stack.push(new Object[] { result });
				return;
			}
			Object[] array0 = stack.peek();
			Object object0 = array0[array0.length - 1];
			android.util.Log.v(this.getClass().getName(), localName + " " + object0);
			if(object0 instanceof List) {
				Object object = newObject(attributes);
				if(object != null) {
					stack.push(new Object[] { object });
					return;
				}
				Class type = decideType(localName, attributes);
				if(type == null) {
					stack.push(new Object[] { null, new StringBuilder() });
					return;
				}
				if(parsers.containsKey(type) || type.isEnum()) {
					stack.push(new Object[] { type, new StringBuilder() });
					return;
				}
				object = newObject(type, null);
				if(object != null) {
					stack.push(new Object[] { object });
					return;
				}
				if(List.class.isAssignableFrom(type)) {
					stack.push(new Object[] { new ArrayList() });
					return;
				}
				if(Map.class.isAssignableFrom(type)) {
					stack.push(new Object[] { new HashMap() });
					return;
				}
			}
			if(object0 instanceof Map) {
				String key = localName;
				Object object = newObject(attributes);
				if(object != null) {
					stack.push(new Object[] { key, object });
					return;
				}
				Class type = decideType(null, attributes);
				if(type == null) {
					stack.push(new Object[] { key, null, new StringBuilder() });
					return;
				}
				if(parsers.containsKey(type) || type.isEnum()) {
					stack.push(new Object[] { key, type, new StringBuilder() });
					return;
				}
				object = newObject(type, null);
				if(object != null) {
					stack.push(new Object[] { key, object });
					return;
				}
				if(List.class.isAssignableFrom(type)) {
					stack.push(new Object[] { key, new ArrayList() });
					return;
				}
				if(Map.class.isAssignableFrom(type)) {
					stack.push(new Object[] { key, new HashMap() });
					return;
				}
			}
			Object loader = findLoader(object0, localName);
			if(loader == null) {
				throw new RuntimeException(localName);
			}
			Object object = newObject(attributes);
			if(object != null) {
				stack.push(new Object[] { loader, object });
				return;
			}
			Class type = decideType(null, attributes);
			if(type == null) {
				if(loader instanceof Method) {
					type = ((Method)loader).getParameterTypes()[0];
				} else if(loader instanceof Field) {
					type = ((Field)loader).getType();
				}
			}
			if(parsers.containsKey(type) || type.isEnum()) {
				stack.push(new Object[] { loader, type, new StringBuilder() });
				return;
			}
			object = initValue(object0, localName);
			if(object != null) {
				stack.push(new Object[] { loader, object });
				return;
			}
			object = newObject(type, object0);
			if(object != null) {
				stack.push(new Object[] { loader, object });
				return;
			}
			if(List.class.isAssignableFrom(type)) {
				stack.push(new Object[] { loader, new ArrayList() });
				return;
			}
			if(Map.class.isAssignableFrom(type)) {
				stack.push(new Object[] { loader, new HashMap() });
				return;
			}
		}
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			Object[] array = stack.peek();
			Object object = array[array.length - 1];
			if(object instanceof StringBuilder) {
				StringBuilder s = (StringBuilder)object;
				s.append(ch, start, length);
			}
		}
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			Object[] array = stack.pop();
			if(stack.isEmpty()) {
				result = array[0];
				return;
			}
			Object[] array0 = stack.peek();
			Object object0 = array0[array0.length - 1];
			android.util.Log.v(this.getClass().getName(), localName + " " + object0);
			if(object0 instanceof List) {
				Object object = array[array.length - 1];
				if(object instanceof StringBuilder) {
					Class type = (Class)array[array.length - 2];
					if(type == null) {
						object = guess(object.toString());
					} else if(type.isEnum()) {
						object = Enum.valueOf(type, object.toString());
					} else {
						Parser parser = parsers.get(type);
						if(parser != null) {
							object = parser.parse(object.toString());
						}
					}
				}
				((List)object0).add(object);
				return;
			}
			if(object0 instanceof Map) {
				Object object = array[array.length - 1];
				if(object instanceof StringBuilder) {
					Class type = (Class)array[array.length - 2];
					if(type == null) {
						object = guess(object.toString());
					} else if(type.isEnum()) {
						object = Enum.valueOf(type, object.toString());
					} else {
						Parser parser = parsers.get(type);
						if(parser != null) {
							object = parser.parse(object.toString());
						}
					}
				}
				((Map)object0).put(array[0], object);
				return;
			}
			Object object = array[array.length - 1];
			if(object instanceof StringBuilder) {
				Class type = (Class)array[array.length - 2];
				if(type == null) {

				} else if(type.isEnum()) {
					object = Enum.valueOf(type, object.toString());
				} else {
					Parser parser = parsers.get(type);
					if(parser != null) {
						object = parser.parse(object.toString());
					}
				}
			}
			Object loader = array[0];
			if(loader instanceof Method) {
				try {
					((Method)loader).invoke(object0, object);
				} catch(Exception x) {
					throw new RuntimeException(x);
				}
			}
			if(loader instanceof Field) {
				try {
					((Field)loader).set(object0, object);
				} catch(Exception x) {
					throw new RuntimeException(x);
				}
			}
		}
	}

	SAXParserFactory parserFactory;
	Map<String, Class> classMappings, classAliases;

	public DataAccessLayer() {
		parserFactory = SAXParserFactory.newInstance();
		classMappings = new HashMap<String, Class>();
		classMappings.put("battle", cn.ching.srpg_demo.game.battle.BattleScene.class);
		classMappings.put("battleground", cn.ching.srpg_demo.game.battle.Battleground.class);
		classMappings.put("party", cn.ching.srpg_demo.game.battle.Party.class);
		classMappings.put("battler", cn.ching.srpg_demo.game.battle.Battler.class);
		classMappings.put("character", cn.ching.srpg_demo.game.battle.Character.class);
		classMappings.put("weapon", cn.ching.srpg_demo.game.battle.Weapon.class);
		classAliases = new HashMap<String, Class>();
		classAliases.put("string", java.lang.String.class);
		classAliases.put("boolean", java.lang.Boolean.class);
		classAliases.put("int", java.lang.Integer.class);
		classAliases.put("int[]", int[].class);
		classAliases.put("int[][]", int[][].class);
		classAliases.put("float", java.lang.Float.class);
	}

	public Object load(int id) {
		return load(id, null);
	}

	public Object load(int id, Object result) {
		InputStream in = Resource.context.getResources().openRawResource(id);
		if(in == null) {
			return null;
		}
		try {
			SAXParser parser = parserFactory.newSAXParser();
			Handler handler = new Handler();
			handler.result = result;
			parser.parse(in, handler);
			return handler.result;
		} catch(Exception x) {
			throw new RuntimeException(x);
		} finally {
			try {
				in.close();
			} catch(IOException x) {
				throw new RuntimeException(x);
			}
		}
	}

	private Class decideType(String nodeName, Attributes attributes) {
		Class type = null;
		String className = attributes.getValue("class");
		if(className != null && className.length() > 0) {
			type = classAliases.get(className);
			if(type == null) {
				try {
					type = Class.forName(className);
				} catch(ClassNotFoundException x) {
					throw new RuntimeException(x);
				}
			}
		}
		if(type == null && nodeName != null) {
			Object value = classMappings.get(nodeName);
			if(value != null || value instanceof Class) {
				type = (Class)value;
			}
		}
		return type;
	}

	private Object newObject(Attributes attributes) {
		if(attributes == null) {
			return null;
		}
		String id = attributes.getValue("id");
		if(id != null && id.length() > 0) {
			Integer objectId = Resource.getObjectId(id);
			if(objectId != null) {
				android.util.Log.i(this.getClass().getName(), "loading " + id);
				return load(objectId);
			} else {
				return new Integer(Resource.getImageId(id));
			}
		}
		return null;
	}

	private Object newObject(Class type, Object root) {
		if(root != null) {
			try {
				Constructor constructor = type.getConstructor(root.getClass());
				return constructor.newInstance(root);
			} catch(Exception x) {
				//just try inner-class first
			}
		}
		try {
			return type.newInstance();
		} catch(Exception x) {
			return null;
		}
	}

	private Method findMethod(Object root, String verb, String name, int args) {
		StringBuilder s = new StringBuilder(name);
		s.setCharAt(0, java.lang.Character.toUpperCase(name.charAt(0)));
		s.insert(0, verb);
		String methodName = s.toString();
		try {
			Method[] methods = root.getClass().getMethods();
			for(Method method : methods) {
				if(method.getName().equals(methodName) && method.getParameterTypes().length == args) {
					return method;
				}
			}
			return null;
		} catch(Exception x) {
			return null;
		}
	}

	private Field findField(Object root, String name) {
		try {
			return root.getClass().getField(name);
		} catch(Exception x) {
			return null;
		}
	}

	private Object findLoader(Object root, String name) {
		Object loader = findMethod(root, "load", name, 1);
		if(loader != null) {
			return loader;
		}
		return findField(root, name);
	}

	private Object initValue(Object root, String name) {
		Method method = findMethod(root, "init", name, 0);
		if(method != null) {
			try {
				return method.invoke(root);
			} catch(Exception x) {
				throw new RuntimeException(x);
			}
		}
		Field field = findField(root, name);
		if(field != null) {
			try {
				return field.get(root);
			} catch(Exception x) {
				throw new RuntimeException(x);
			}
		}
		return null;
	}

}
