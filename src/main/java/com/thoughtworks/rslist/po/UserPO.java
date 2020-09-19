package com.thoughtworks.rslist.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@Table(name = "user")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    private String userName;
    private String gender;
    private int age;
    private String email;
    private String phone;
    @Builder.Default
    private int voteNumber=10;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "userPO")
    private List<RsEventPO> rsEventPOs;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPO userPO = (UserPO) o;
        return id == userPO.id &&
                age == userPO.age &&
                voteNumber == userPO.voteNumber &&
                Objects.equals(userName, userPO.userName) &&
                Objects.equals(gender, userPO.gender) &&
                Objects.equals(email, userPO.email) &&
                Objects.equals(phone, userPO.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, gender, age, email, phone, voteNumber, rsEventPOs);
    }

    @Override
    public String toString() {
        return "UserPO{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", voteNumber=" + voteNumber +
                '}';
    }
}
