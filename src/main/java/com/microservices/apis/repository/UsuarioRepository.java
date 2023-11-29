package com.microservices.apis.repository;



import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.microservices.apis.model.Usuario;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("select u from Usuario u where u.login = ?1")
    Usuario findUserByLogin(String login);

    @Query("select u from Usuario u where u.nome like %?1%")
    List<Usuario> findUserByName(String nome);

    @Transactional
    @Modifying
	@Query(nativeQuery = true, value="update usuario set token = ?1 where login = ?2")
	void atualizaTokenUser(String token, String login);

    @Query(value = "SELECT constraint_name from information_schema.constraint_column_usage\n" +
            "\tWHERE table_name = 'usuarios_role' and column_name = 'role_id'\n" +
            "\tand constraint_name <> 'unique_role_user';", nativeQuery = true)
    String cosultaConstraintRole();

//    @Modifying
//    @Query(value = "ALTER TABLE usuarios_role DROP CONSTRAINT ?1;", nativeQuery = true)
//    void removeConstraintRole(String constraint);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO usuarios_role(usuario_id, role_id)\n" +
            "\tvalues(?1,(SELECT id from role where nome_role = 'ROLE_USER'));")
    void insertAccessRolePadrao(Long idUser);


    default Page<Usuario> findUserByNamePage(String nome, PageRequest pageRequest){
        Usuario usuario = new Usuario();
        usuario.setNome(nome);

        //Configurando para pesquisar por nome e paginação
        ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
                .withMatcher("nome", ExampleMatcher.GenericPropertyMatchers
                        .contains().ignoreCase());
        Example<Usuario> example = Example.of(usuario, exampleMatcher);

        Page<Usuario> usuarioPage = findAll(example, pageRequest);

        return usuarioPage;
    }
}
