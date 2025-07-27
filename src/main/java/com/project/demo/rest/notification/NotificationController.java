package com.project.demo.rest.notification;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.notification.Notification;
import com.project.demo.logic.entity.notification.NotificationRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','USER')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            HttpServletRequest request) {

        try {

            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Notification> notificationsPage;

            if (search == null || search.trim().isEmpty()) {
                notificationsPage = notificationRepository.findAll(pageable);
            } else {
                notificationsPage = notificationRepository.searchNotifications(search.trim(), pageable);
            }

            Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
            meta.setTotalPages(notificationsPage.getTotalPages());
            meta.setTotalElements(notificationsPage.getTotalElements());
            meta.setPageNumber(notificationsPage.getNumber() + 1);
            meta.setPageSize(notificationsPage.getSize());

            return new GlobalResponseHandler().handleResponse(
                    "Notificaciones recuperadas exitosamente",
                    notificationsPage.getContent(),
                    HttpStatus.OK,
                    meta
            );
        }catch (Exception e){
            return new GlobalResponseHandler().handleResponse("Error al recuperar las notificaciones: "+
                            e.getMessage(),
                    null,
                    HttpStatus.NO_CONTENT,
                    request);
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> create(@RequestBody Notification notification, HttpServletRequest request) {
        try {
            Notification savedNotification = notificationRepository.save(notification);

            return new GlobalResponseHandler().handleResponse("Notificacion creada con exito",
                    savedNotification, HttpStatus.CREATED, request);
        }catch (Exception e){
            return new GlobalResponseHandler().handleResponse("Error al crear la notificacion: "+e.getMessage(),null,
                    HttpStatus.INTERNAL_SERVER_ERROR,request);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateNotification(@PathVariable("id") Long id, @RequestBody Notification notification, HttpServletRequest request) {
        try {
            Optional<Notification> foundNotification = notificationRepository.findById(id);

            if (foundNotification.isEmpty()){
                return new GlobalResponseHandler().handleResponse("Notificacion no encontrada",
                        null,HttpStatus.NOT_FOUND,request);
            }

            Notification updatedNotification = foundNotification.get();

            updatedNotification.setName(notification.getName());
            updatedNotification.setDescription(notification.getDescription());
            updatedNotification.setType(notification.getType());
            updatedNotification.setState(notification.getState());
            updatedNotification.setDate(notification.getDate());

            notificationRepository.save(updatedNotification);

            return new GlobalResponseHandler().handleResponse("Notificacion actualizada con exito"
                    ,updatedNotification,HttpStatus.OK,request);

        }catch (Exception e){
            return new GlobalResponseHandler().handleResponse("Ocurrio un error al actualizar la notificacion: "+
                            e.getMessage(),
                    null, HttpStatus.INTERNAL_SERVER_ERROR,request);
        }
    }

    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long notificationId, HttpServletRequest request) {
        Optional<Notification> foundNotification = notificationRepository.findById(notificationId);
        if(foundNotification.isPresent()) {
            notificationRepository.deleteById(notificationId);
            return new GlobalResponseHandler().handleResponse("Notificacion eliminada exitosamente",
                    foundNotification.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Factura con id: " + notificationId + " no encontrado"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }
}
