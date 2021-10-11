package com.example.mywebquizengine.Controller.api;

import com.example.mywebquizengine.Controller.ChatController;
import com.example.mywebquizengine.Controller.GeoController;
import com.example.mywebquizengine.Controller.UserController;
import com.example.mywebquizengine.Model.Chat.Dialog;
import com.example.mywebquizengine.Model.Chat.MessageStatus;
import com.example.mywebquizengine.Model.Geo.Geolocation;
import com.example.mywebquizengine.Model.Geo.Meeting;
import com.example.mywebquizengine.Model.Projection.Api.MeetingForApiView;
import com.example.mywebquizengine.Model.Projection.Api.MessageForApiViewCustomQuery;
import com.example.mywebquizengine.Model.Request;
import com.example.mywebquizengine.Model.UserInfo.AuthRequest;
import com.example.mywebquizengine.Model.UserInfo.AuthResponse;
import com.example.mywebquizengine.Model.UserInfo.GoogleToken;
import com.example.mywebquizengine.Model.Projection.*;

import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Repos.*;
import com.example.mywebquizengine.Service.JWTUtil;
import com.example.mywebquizengine.Service.MessageService;
import com.example.mywebquizengine.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;

import javax.transaction.Transactional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.*;

@RestController
public class ApiController {

    private static final HttpTransport transport = new NetHttpTransport();
    private static final JsonFactory jsonFactory = new JacksonFactory();

    @Value("${androidGoogleClientId}")
    private String CLIENT_ID;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtil jwtTokenUtil;

    @Autowired
    private MessageService messageService;

    @Autowired
    private DialogRepository dialogRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Value("${hostname}")
    private String hostname;

    @Autowired
    private GeoController geoController;

    @Autowired
    private UserController userController;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ChatController chatController;

    @Transactional
    @GetMapping(path = "/api/messages")
    //@PreAuthorize(value = "@dialogRepository.findDialogByDialogId(#dialogId).users/*проверить содержание тут надо*/.contains(principal.name)")
    public DialogWithUsersViewPaging getMessages(@RequestParam Long dialogId,
                                                 @RequestParam(required = false,defaultValue = "0") @Min(0) Integer page,
                                                 @RequestParam(required = false,defaultValue = "50") @Min(1) @Max(100) Integer pageSize,
                                                 @RequestParam(defaultValue = "timestamp") String sortBy,
                                                 @AuthenticationPrincipal Principal principal) {

        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy).descending());



        if (dialogRepository.findDialogByDialogId(dialogId).getUsers().stream().anyMatch(o -> o.getUsername()
                .equals(principal.getName()))) {

            //DialogWithUsersViewPaging allDialogByDialogId = dialogRepository.findAllDialogByDialogId(dialogId);

            dialogRepository.findById(dialogId).get().setPaging(paging);

            //PageRequest.of()
            //allDialogByDialogId.setMessages(messageRepository.findAllByDialog_DialogId(dialogId, paging));


            //Page<MessageView> allByDialog_dialogId = messageRepository.findAllByDialog_DialogId(dialogId, paging);
            //allDialogByDialogId.setMessages();

            return dialogRepository.findAllDialogByDialogId(dialogId);
        } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        //return dialog.getMessages();
    }



    /*
        Вложенные поля проекции не инициализируются при пользовательском нативном запросе

        На данный момент работает следующим образом:
            Пользовательским запросом получается список диалогов, где диалог представлен только id сообщения,
            далее по всем этим id делается встроенный findById и выводятся диалоги

        В идеале необходимо решить проблему с инициализацией вложенных полей
     */
    @GetMapping(path = "/api/dialogs")
    public ArrayList<MessageForApiViewCustomQuery> getDialogs(@AuthenticationPrincipal Principal principal) {

        return messageService.getDialogsForApi(principal.getName());
    }

    @GetMapping(path = "/api/getDialogId")
    //@ResponseBody
    public Long checkDialog(@RequestParam String username, @AuthenticationPrincipal Principal principal) {

        return messageService.checkDialog(userService.loadUserByUsername(username), principal.getName());
    }


    @GetMapping(path = "/api/authuser")
    public UserView getApiAuthUser() {

        final String authorizationHeader = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest().getHeader("Authorization");
        String username = null;
        String jwt = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            //если подпись не совпадает с вычисленной, то SignatureException
            //если подпись некорректная (не парсится), то MalformedJwtException
            //если время подписи истекло, то ExpiredJwtException
            username = jwtTokenUtil.extractUsername(jwt);
        }

        return userRepository.findAllByUsername(username);
    }

    @GetMapping(path = "/api/findbyid")
    public UserCommonView getUserById(@RequestParam String username) {
        return userRepository.findByUsername(username);
    }


    @PostMapping(path = "/api/signin")
    public AuthResponse jwt(@RequestBody AuthRequest authRequest) {

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            //System.out.println(authentication);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Имя или пароль неправильны", e);
        }
        // при создании токена в него кладется username как Subject claim и список authorities как кастомный claim
        String jwt = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());
        return new AuthResponse(jwt);
    }



    @PostMapping(path = "/api/signup")
    //@ResponseBody
    public AuthResponse signup(@RequestBody User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveUser(user);

        // при создании токена в него кладется username как Subject claim и список authorities как кастомный claim
        String jwt = jwtTokenUtil.generateToken(userService.loadUserByUsername(user.getUsername()));
        return new AuthResponse(jwt);
    }




    @PostMapping(path = "/api/googleauth")
    public AuthResponse googleJwt(@RequestBody GoogleToken token) throws GeneralSecurityException, IOException {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(CLIENT_ID))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

        GoogleIdToken idToken = verifier.verify(token.getIdTokenString());
        if (idToken != null) {
            Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");



            String username = email.replace("@gmail.com", "");

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(givenName);
            user.setLastName(familyName);
            user.setAvatar(pictureUrl);

            userService.tryToSaveUser(user);

            User savedUser = userService.loadUserByUsername(user.getUsername());

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            username, null, savedUser.getAuthorities()); // Проверить работу getAuthorities
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            String jwt = jwtTokenUtil.generateToken(savedUser);
            return new AuthResponse(jwt);
            // Use or store profile information
            // ...

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        }

    }

    @GetMapping(path = "/api/meetings")
    public ArrayList<MeetingForApiView> getMyMeetings(@AuthenticationPrincipal Principal principal) {

        User authUser = userService.loadUserByUsername(principal.getName());

        Calendar calendar = new GregorianCalendar();
        Timestamp date = Timestamp.from(calendar.toInstant());

        //ArrayList<Geolocation> peopleNearMe = findInSquare(SecurityContextHolder.getContext().getAuthentication(),"20");


        return (ArrayList<MeetingForApiView>) meetingRepository.getMyMeetingsTodayApi(authUser.getUsername(),
                date.toString().substring(0,10) + " 00:00:00",
                date.toString().substring(0,10) + " 23:59:59");
        /*return (ArrayList<Meeting>) meetingRepository.getMyMeetingsToday(userService.getAuthUserNoProxy
                (SecurityContextHolder.getContext().getAuthentication()).getUsername(),
                        date.toString().substring(0,10) + " 00:00:00",
                date.toString().substring(0,10) + " 23:59:59");*/
        //return "meetings";
    }

    @PostMapping(path = "/api/sendGeolocation")
    public void sendGeolocation(@AuthenticationPrincipal Principal principal, @RequestBody Geolocation myGeolocation) throws JsonProcessingException, ParseException {
        //myGeolocation.setId(principal.getName());




        myGeolocation.setUser(userService.loadUserByUsernameProxy(principal.getName()));
        Calendar calendar = new GregorianCalendar();

        //geolocation.setId(geolocation.getUser().getUsername());
        userService.saveGeo(myGeolocation);



        //System.out.println(date.toString().substring(0,10));

        ArrayList<Geolocation> peopleNearMe = geoController.findInSquare(principal.getName(),myGeolocation, "20");

        if (peopleNearMe.size() > 0) {

            for (int i = 0; i < peopleNearMe.size(); i++) {

                if (meetingRepository.
                        getMeetings(myGeolocation.getUser().getUsername(),
                                peopleNearMe.get(i).getUser().getUsername())
                        .size() == 0) {

                    Meeting meeting = new Meeting();
                    meeting.setFirstUser(myGeolocation.getUser());
                    meeting.setSecondUser(peopleNearMe.get(i).getUser());
                    meeting.setLat(myGeolocation.getLat());
                    meeting.setLng(myGeolocation.getLng());
                    meeting.setTime(calendar);

                    meetingRepository.save(meeting);

                    JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(objectMapper.writeValueAsString(meetingRepository.findMeetingById(meeting.getId())));
                    jsonObject.put("type", "MEETING");

                    simpMessagingTemplate.convertAndSend("/topic/" + myGeolocation.getUser().getUsername(), jsonObject);

                    rabbitTemplate.setExchange("message-exchange");

                    rabbitTemplate.convertAndSend(myGeolocation.getUser().getUsername(), jsonObject);




                /*    ResponseEntity
                            .ok()
                            .header("type", "meeting")
                            .body(meetingRepository.findMeetingById(meeting.getId()))*/

                    /*message -> {
                        message.getMessageProperties().setHeader("type", "MEETING");
                        return message;
                    })*/
                }

            }

            /*userController.testConnection(principal);*/


        }
    }

    @PostMapping(path = "/api/upload")
    public void handleFileUpload(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal Principal principal) {
       // String name = file.getOriginalFilename();

        if (!file.isEmpty()) {
            try {
                String uuid = UUID.randomUUID().toString();
                uuid = uuid.substring(0, 8);
                byte[] bytes = file.getBytes();

                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File("img/" +
                                uuid + ".jpg")));
                stream.write(bytes);
                stream.close();

                User user = userService.loadUserByUsernameProxy(principal.getName());

                userService.setAvatar("https://" + hostname + "/img/" + uuid + ".jpg", user);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Transactional
    @PutMapping(path = "/api/update/user/{username}", consumes={"application/json"})
    @PreAuthorize(value = "#principal.name.equals(#username)")
    public void changeUser(@PathVariable String username, @RequestBody User user,
                           @AuthenticationPrincipal Principal principal) {
        userService.updateUser(user.getLastName(), user.getFirstName(), username);
    }


    /*@GetMapping(path = "/api/test")
    public ResponseEntity<MeetingView> test() {
        return ResponseEntity
                .ok()
                .header("type", "meeting")
                .body(meetingRepository.findMeetingById(46364L));
    }*/


    @PostMapping(path = "/api/sendRequest")
    @ResponseBody
    //@ResponseStatus(HttpStatus.OK)
    public void sendRequest(@RequestBody Request request, @AuthenticationPrincipal Principal principal) {
        request.setSender(userService.loadUserByUsername(principal.getName()));

        Long dialogId = chatController.checkDialog(request.getTo(), principal);

        Dialog dialog = new Dialog();
        dialog.setDialogId(dialogId);
        request.getMessage().setDialog(dialog);
        request.getMessage().setSender(userService.loadUserByUsernameProxy(principal.getName()));
        request.getMessage().setStatus(MessageStatus.DELIVERED);
        request.getMessage().setTimestamp(new GregorianCalendar());

        requestRepository.save(request);
        throw new ResponseStatusException(HttpStatus.OK);
    }

    @PostMapping(path = "/api/rejectRequest")
    @ResponseBody
    //@PreAuthorize(value = "!#principal.name.equals(#user.username)")
    public void rejectRequest(@RequestBody Request requestId, @AuthenticationPrincipal Principal principal) {
        Request request = requestRepository.findById(requestId.getId()).get();
        request.setStatus("REJECTED");
        requestRepository.save(request);
        throw new ResponseStatusException(HttpStatus.OK);
    }

    @PostMapping(path = "/api/acceptRequest")
    @ResponseBody
    //@PreAuthorize(value = "!#principal.name.equals(#user.username)")
    public Long acceptRequest(@RequestBody Request requestId, @AuthenticationPrincipal Principal principal) {
        User authUser = userService.loadUserByUsername(principal.getName());


        Request request = requestRepository.findById(requestId.getId()).get();
        request.setStatus("ACCEPTED");

        authUser.addFriend(request.getSender());
        requestRepository.save(request);
        Long dialog_id = messageService.checkDialog(request.getSender(), principal.getName());

        if (dialog_id == null) {
            Dialog dialog = new Dialog();
            //  Set<User> users = new HashSet<>();
            dialog.addUser(userService.loadUserByUsername(request.getSender().getUsername()));
            dialog.addUser(authUser);
//            users.add(userService.loadUserByUsername(user.getUsername()));
//            users.add(userService.getAuthUserNoProxy(SecurityContextHolder.getContext().getAuthentication()));
            //dialog.setUsers(users);
            dialogRepository.save(dialog);
            return dialog.getDialogId();
        } else {
            return dialog_id;
        }
    }

    @GetMapping(path = "/api/requests")
    public ArrayList<RequestView> getMyRequests(Model model, @AuthenticationPrincipal Principal principal) {

        //User authUser = userService.loadUserByUsername(principal.getName());

        return requestRepository.findAllByToUsernameAndStatus(principal.getName(), "PENDING");
    }

}
