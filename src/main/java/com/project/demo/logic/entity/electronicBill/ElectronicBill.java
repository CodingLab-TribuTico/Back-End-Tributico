package com.project.demo.logic.entity.electronicBill;


import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.detailsBill.DetailsBill;

@Entity
@Table(name = "electronic_bill")
public class ElectronicBill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long consecutive;
    private LocalDate issueDate;
    private int code;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "electronicBill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailsBill> details;



    public ElectronicBill() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConsecutive() {
        return consecutive;
    }

    public void setConsecutive(Long consecutive) {
        this.consecutive = consecutive;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<DetailsBill> getDetails() {
        return details;
    }

    public void setDetails(List<DetailsBill> details) {
        this.details = details;
    }


}
