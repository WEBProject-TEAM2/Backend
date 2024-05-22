package Chapter_04;

import java.util.Scanner;

class Student {
    String name;
    // 명시를 안했을 경우 :default - 동일한 패키지 안에서 접근 가능
    public int age;
    public String addr;
}

public class EX12 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Student student = new Student();

        System.out.print("이름 : ");
        student.name = scanner.next();
        System.out.print("나이 : ");
        student.age = scanner.nextInt();
        System.out.print("주소 : ");
        student.addr = scanner.next();

        System.out.println("이름 : " + student.name);
        System.out.println("나이 : " + student.age);
        System.out.println("주소 : " + student.addr);
        scanner.close();
    }
}
