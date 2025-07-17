package com.project.demo.logic.entity.invoice;


import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.detailsInvoice.DetailsInvoice;

@Entity
@Table(name = "inovice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int consecutive;
    private LocalDate issueDate;
    private int code;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailsInvoice> details;



    public Invoice() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getConsecutive() {
        return consecutive;
    }

    public void setConsecutive(int consecutive) {
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

    public List<DetailsInvoice> getDetails() {
        return details;
    }

    public void setDetails(List<DetailsInvoice> details) {
        this.details = details;
    }


}
