package fr.certu.chouette.dao.hibernate;

public class Mixin {
	
	Object [] objects = null;

	public Mixin(Object ... objects) {
		objects = new Object[objects.length];
		this.objects = objects;
	}

	public Mixin(Object object) {
		objects = new Object[1];
		this.objects[0] = object;
	}

	public Mixin(Object object1, Object object2) {
		objects = new Object[2];
		this.objects[0] = object1;
		this.objects[1] = object2;
	}
	
	public String toString() {
		return "TO DO";
	}
	
	public boolean equals(Object object) {
		for (int idx = 0; idx < objects.length; idx++) {
			if (!object.equals(objects[idx])) {
				return false;
			}
		}
		return true;
	}
	
	public Object get (int index) {
		return objects[index];
	}
}
