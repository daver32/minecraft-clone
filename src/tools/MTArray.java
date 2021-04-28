package tools;

import java.util.ArrayList;
import java.util.Iterator;

//multi-threaded array
public class MTArray<E> implements Iterable<E>{

	private ArrayList<E> array = new ArrayList<E>();
	private boolean locked = false;
	
	private boolean duplicateLock = false;
	private boolean nullLock = false;
	
	private int maxSize = -1;
	
	public MTArray(boolean duplicateLock){
		this.duplicateLock = duplicateLock;
	}
	
	private MTArray(ArrayList<E> array, int maxSize, boolean duplicateLock){
		this.array = array;
		this.maxSize = maxSize;
		this.duplicateLock = duplicateLock;
	}
	
	public MTArray(int maxSize, boolean duplicateLock){
		this.maxSize = maxSize;
		this.duplicateLock = duplicateLock;
	}
	
	public MTArray(){
		
	}
	
	public MTArray(int maxSize){
		this.maxSize = maxSize;
	}
	
	public int size(){
		return array.size();
	}
	
	public E get(int index){
		try{
			waitForUnlock();
			locked = true;
			E res = array.get(index);
			locked = false;
			return res;
		}catch(IndexOutOfBoundsException e){
			locked = false;
			return null;
		}

	}
	
	public boolean add(E element){
		if(maxSize != -1 && array.size() == maxSize){
			return false;
		}
		if(duplicateLock && array.contains(element)){
			return false;
		}
		if(nullLock && element == null){
			return false;
		}
		
		waitForUnlock();
		locked = true;
		boolean res = array.add(element);
		locked = false;
		return res;
	}
	
	public boolean remove(E element){
		waitForUnlock();
		locked = true;
		boolean res = array.remove(element);
		locked = false;
		return res;
	}
	
	public E remove(int index){
		waitForUnlock();
		locked = true;
		E res = array.remove(index);
		locked = false;
		return res;
	}
	
	public void clear(){
		waitForUnlock();
		locked = true;
		array.clear();
		locked = false;
	}
	
	public int removeNulls(){
		waitForUnlock();
		locked = true;
		int res = 0;
		while(array.remove(null)){
			res++;
		}
		locked = false;
		return res;
	}
	
	public boolean contains(E element){
		return array.contains(element);
	}
	
	@Override
	public Iterator<E> iterator() {
		waitForUnlock();
		locked = true;
		Iterator<E> res = array.iterator();
		locked = false;
		return res;
	}
	
	private void waitForUnlock(){
		while(locked){
			try {
				Thread.sleep(0, 1000);
			} catch (InterruptedException e) {}
		}
	}
	
	@SuppressWarnings("unchecked")
	public MTArray<E> clone(){
		waitForUnlock();
		locked = true;
		MTArray<E> res = new MTArray<E>((ArrayList<E>)array.clone(), maxSize, duplicateLock);
		locked = false;
		return res;
	}

	public boolean isNullLock() {
		return nullLock;
	}

	public void setNullLock(boolean nullLock) {
		this.nullLock = nullLock;
	}

	
}
