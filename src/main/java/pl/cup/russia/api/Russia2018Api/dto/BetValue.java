package pl.cup.russia.api.Russia2018Api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BetValue {

    private String winner;

    private String groupName;

    private String firstPlace;

    private String secondPlace;

    private Integer matchId;

    private Integer hometeamScore;

    private Integer awayteamScore;

    public BetValue(String winner) {
        this.winner = winner;
    }

    public BetValue(String firstPlace, String groupName, String secondPlace) {
        this.firstPlace = firstPlace;
        this.groupName = groupName;
        this.secondPlace = secondPlace;
    }

}
