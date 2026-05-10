package com.ansari.linkedin.connections_service.service;

import com.ansari.linkedin.connections_service.auth.AuthContextHolder;
import com.ansari.linkedin.connections_service.entity.Person;
import com.ansari.linkedin.connections_service.exception.BadRequestException;
import com.ansari.linkedin.connections_service.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionsService {

    private final PersonRepository personRepository;

    public List<Person> getFirstDegreeConnections(Long userId) {
        log.info("Retrieving first degree connections for user with id: {}", userId);
        return personRepository.getFirstDegreeConnections(userId);
    }

    public void sendConnectionRequest(Long receiverId){

        Long senderId = AuthContextHolder.getCurrentUserId();

        log.info("sending connection request with senderId:{} , recieverId:",senderId,receiverId);

        if(senderId.equals(receiverId)){
            throw new BadRequestException("Both sender and receiver are the same");
        }
        boolean alreadyConnected = personRepository.alreadyConnected(senderId,receiverId);

        if(alreadyConnected){
            throw new BadRequestException("Already connected users, cannot add connection request");
        }

        boolean alreadySentRequest = personRepository.connectionRequestExists(senderId,receiverId);

        if(alreadySentRequest){
            throw new BadRequestException("Connection request already exists, cannont send again");
        }

        log.info("Succesfully sent the connection request");

        personRepository.addConnectionRequest(senderId, receiverId);

    }

    public void acceptConnectionRequest(Long senderId){
        Long receiverId = AuthContextHolder.getCurrentUserId();

        log.info("accepting connection request with senderId:{} , recieverId:",senderId,receiverId);

        if(senderId.equals(receiverId)){
            throw new BadRequestException("Both sender and receiver are the same");
        }

        boolean alreadySentRequest = personRepository.connectionRequestExists(senderId,receiverId);

        if(!alreadySentRequest){
            throw new BadRequestException("No Connection request exists, cannot accept without request.");
        }

        boolean alreadyConnected = personRepository.alreadyConnected(senderId,receiverId);

        if(alreadyConnected){
            throw new BadRequestException("Already connected users, cannot accept connection request again.");
        }

        log.info("Succesfully accepted the connection request with senderId:{} , recieverId:",senderId,receiverId);

        personRepository.acceptConnectionRequest(senderId, receiverId);
    }

    public void rejectConnectionRequest(Long senderId){
        Long receiverId = AuthContextHolder.getCurrentUserId();

        log.info("rejecting connection request with senderId:{} , recieverId:",senderId,receiverId);

        if(senderId.equals(receiverId)){
            throw new BadRequestException("Both sender and receiver are the same");
        }

        boolean alreadySentRequest = personRepository.connectionRequestExists(senderId,receiverId);

        if(!alreadySentRequest){
            throw new BadRequestException("No Connection request exists, cannot reject without request.");
        }

        boolean alreadyConnected = personRepository.alreadyConnected(senderId,receiverId);

        if(alreadyConnected){
            throw new BadRequestException("Already connected users, cannot reject connection.");
        }

        log.info("Succesfully rejected the connection request with senderId:{} , recieverId:",senderId,receiverId);

        personRepository.rejectConnectionRequest(senderId, receiverId);
    }
}
