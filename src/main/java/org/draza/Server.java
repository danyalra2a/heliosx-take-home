package org.draza;

import io.javalin.Javalin;
import org.draza.consultation.Controller;
import org.draza.consultation.Repository;
import org.draza.consultation.Service;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Server {
  public static void main(String[] args) {
    Repository repository = new Repository();
    Service service = new Service(repository);
    Controller controller = new Controller(service);

    Javalin app = Javalin.create(config -> {
      config.router.apiBuilder(() -> {
        path("/consultation", () -> {
          get("{consultationId}", controller::getConsultationQuestions);
          post("{consultationId}", controller::postConsultationAnswers);
          get("{submissionId}/result", controller::getConsultationStatus);
        });
      });
    });

    app.start(8088);
  }
}
