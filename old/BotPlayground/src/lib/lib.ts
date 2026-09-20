
export abstract class Iter<T> {
	public map<NT>(func: (T) => NT): Iter<NT> {
		return new MapIter<T, NT>(this, func);
	}

	public filter(func: (T) => boolean): Iter<T> {
		return new FilterIter(this, func);
	}

	public sum(): number {
		return this.sum_impl()[0]
	}

	public avg(): number {
		let [acc, len] = this.sum_impl();
		return acc / len;
	}

	abstract next(): T | null;

	private sum_impl(): [number, number] {
		let acc = 0;
		let len = 0;

		let next: T | null = this.next();
		do {
			len++;
			acc += next as number;
			next = this.next();
		} while (next);
		return [acc, len];
	}
}

class MapIter<A, B> extends Iter<B> {
	private child: Iter<A>;
	private readonly func: (A) => B;

	constructor(it: Iter<A>, func: (A) => B) {
		super();
		this.child = it;
		this.func = func;
	}

	next(): B | null {
		return this.func(this.child.next());
	}
}

class FilterIter<T> extends Iter<T> {
	private child: Iter<T>;
	private readonly func: (T) => boolean;

	constructor(it: Iter<T>, func: (T) => boolean) {
		super();
		this.child = it;
		this.func = func;
	}

	next(): T | null {
		let next: T | null = null;
		while (!next || !this.func(next))
			next = this.child.next();
		return next;
	}
}

export class ArrayIter<T> extends Iter<T> {
	private readonly arr: T[];
	private ind: number = 0;

	constructor(arr: T[], ind: number) {
		super();
		this.arr = arr;
		this.ind = ind;
	}

	next(): T | null {
		return this.ind < this.arr.length ? this.arr[this.ind++] : null;
	}
}



