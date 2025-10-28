package com.planttracker.Services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.planttracker.Models.PlantTypes;
import com.planttracker.Repositories.PlantTypeRepository;

@Service
public class PlantTypeService {

     private final PlantTypeRepository repo;

     public PlantTypeService(PlantTypeRepository repo) {
          this.repo = repo;
     }

     private Authentication getAuth() {
          return SecurityContextHolder.getContext().getAuthentication();
     }

     private boolean isAdmin() {
          Authentication auth = getAuth();
          if (auth == null || auth.getAuthorities() == null)
               return false;
          return auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(r -> r.equals("ROLE_ADMIN"));
     }

     // Paginated list
     public Page<PlantTypes> list(Pageable pageable) {
          return repo.findAll(pageable);
     }

     // Non-paginated list (backward compatibility)
     public List<PlantTypes> list() {
          return repo.findAll();
     }

     public PlantTypes get(Long id) {
          return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
     }

     public PlantTypes create(PlantTypes payload) {
          if (!isAdmin())
               throw new ResponseStatusException(HttpStatus.FORBIDDEN);
          return repo.save(payload);
     }

     public PlantTypes update(Long id, PlantTypes payload) {
          if (!isAdmin())
               throw new ResponseStatusException(HttpStatus.FORBIDDEN);
          PlantTypes existing = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
          existing.setTypeName(payload.getTypeName());
          existing.setDescription(payload.getDescription());
          return repo.save(existing);
     }

     public void delete(Long id) {
          if (!isAdmin())
               throw new ResponseStatusException(HttpStatus.FORBIDDEN);
          if (!repo.existsById(id))
               throw new ResponseStatusException(HttpStatus.NOT_FOUND);
          repo.deleteById(id);
     }
}
