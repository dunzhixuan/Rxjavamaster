package com.zhixuan.app.rxjava_master;

public class Person {
		private String name;
		private int age;

		Person(String name,int age){
				this.name = name;
				this.age = age;
		}

		Person(){}

		public void setName(String name) {
				this.name = name;
		}

		public String getName() {
				return name;
		}

		public void setAge(int age) {
				this.age = age;
		}

		public int getAge() {
				return age;
		}
}
