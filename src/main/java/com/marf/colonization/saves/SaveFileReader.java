package com.marf.colonization.saves;

import com.marf.colonization.reader.BaseReader;
import com.marf.colonization.reader.IoSupplier;
import com.marf.colonization.saves.section.*;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class SaveFileReader extends BaseReader {
    private final SaveFile saveFile;
    private final Tables tables;
    private boolean hasGlobalErrors;

    public SaveFileReader(Tables tables, ImageInputStream stream) {
        super(stream);
        this.tables = tables;
        this.saveFile = new SaveFile();
        this.hasGlobalErrors = false;
    }

    public SaveFile readAll() {
        try {
            stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            stream.seek(0);

            saveFile.setHeader(readObject(Header::new, this::readHeader));
            // Read royal force (4 words = 8 bytes)
            saveFile.setRoyalForce(new ArrayList<>());
            for (int i = 0; i < 4; i++) {
                saveFile.getRoyalForce().add(stream.readUnsignedShort());
            }

            // Read padding (44 bytes)
            saveFile.setPadding7(readBytes(stream, 44));

            // Read players (4 players)
            saveFile.setPlayers(readObjectList(4, Player::new, this::readPlayer));

            // Read padding (24 bytes)
            saveFile.setViewports(readObjectList(4, Viewport::new, this::readViewport));

            // Read colonies (dynamic count)
            saveFile.setColonies(readObjectList(saveFile.getHeader().getNumColonies(), Colony::new, this::readColony));
            // Read units (dynamic count)
            saveFile.setUnits(readObjectList(saveFile.getHeader().getNumUnits(), Unit::new, this::readUnit));

            // Read europe (4 entries)
            saveFile.setEurope(readObjectList(4, Europe::new, this::readEurope));

            // Read tribes (dynamic count)
            saveFile.setIndianVillages(readObjectList(saveFile.getHeader().getNumTribes(), IndianVillage::new, this::readindianVillage));

            // Read indians (8 entries)
            saveFile.setIndianTribes(readObjectList(8, IndianTribe::new, this::readTribe));

            // Read padding (717 bytes)
            saveFile.setPadding9(readBytes(stream, 717));

            // Read cursor position
            saveFile.setCursorPos(readObject(Position::new, this::readPosition));

            // Read padding (2 bytes)
            saveFile.setPadding10(readBytes(stream, 2));

            // Read viewport position
            saveFile.setViewport(readObject(Position::new, this::readPosition));

            // Read map data (dynamic size based on mapSize)
            saveFile.setMap(readObject(GameMap::new, (gameMap, s) -> this.readGameMap(gameMap, s, saveFile.getHeader().getMapSize())));


        } catch (IOException e) {
            addGlobalError("Fatal error reading file: " + e.getMessage());
        }

        saveFile.setHasErrors(hasGlobalErrors || hasAnySectionErrors());
        return saveFile;
    }

    private Header readHeader(Header header, ImageInputStream stream) throws IOException {
        // Read magic string (8 bytes)
        header.setMagic(readString(stream, 8));

        // Read padding (4 bytes)
        header.setPadding1(readBytes(stream, 4));

        // Read map size as Position (2 words = 4 bytes)
        header.setMapSize(readObject(Position::new, this::readPosition));

        // Read padding (10 bytes)
        header.setPadding2(readBytes(stream, 10));

        // Read year, autumn, turn (each 2 bytes)
        header.setYear(stream.readUnsignedShort());
        header.setAutumn(stream.readUnsignedShort());
        header.setTurn(stream.readUnsignedShort());

        // Read padding (2 bytes)
        header.setPadding3(readBytes(stream, 2));

        // Read active unit (2 bytes)
        header.setActiveUnit(stream.readUnsignedShort());

        // Read padding (6 bytes)
        header.setViewportPower(stream.readUnsignedShort());
        header.setPlayerControlledPower(stream.readUnsignedShort());
        header.setMaybe_current_player(stream.readUnsignedShort());

        // Read counts (each 2 bytes)
        header.setNumTribes(stream.readUnsignedShort());
        header.setNumUnits(stream.readUnsignedShort());
        header.setNumColonies(stream.readUnsignedShort());

        // Read padding (6 bytes)
        header.setPadding5(readBytes(stream, 6));

        // Read difficulty (1 byte) - will implement lookup later
        header.setDifficulty(stream.readUnsignedByte());

        // Read padding (51 bytes)
        header.setPadding6(readBytes(stream, 51));
        return header;
    }

    private Position readPosition(Position position, ImageInputStream stream) throws IOException {
        position.setX(stream.readUnsignedShort());
        position.setY(stream.readUnsignedShort());
        return position;
    }

    private Viewport readViewport(Viewport viewport, ImageInputStream stream) throws IOException {
        viewport.setX(stream.readUnsignedShort());
        viewport.setY(stream.readUnsignedShort());
        viewport.setZoom(stream.readUnsignedShort());
        return viewport;
    }


    private Player readPlayer(Player player, ImageInputStream stream) throws IOException {
        // Read name (24 bytes)
        player.setName(readString(stream, 24));

        // Read continent (24 bytes)
        player.setContinent(readString(stream, 24));

        // Read byte1 (1 byte)
        player.setByte1(stream.readUnsignedByte());

        // Read control (1 byte) - use lookup
        player.setControl(readTableValue(tables.getControls(), stream::readUnsignedByte));
        // Read diplomacy (2 bytes)
        player.setDiplomacy(stream.readUnsignedShort());

        return player;
    }

    private Colony readColony(Colony colony, ImageInputStream stream) throws IOException {
        // Read coordinates (1 byte each)
        colony.setX(stream.readUnsignedByte());
        colony.setY(stream.readUnsignedByte());

        // Read name (24 bytes)
        colony.setName(readString(stream, 24));

        // Read nation (1 byte)
        colony.setNation(stream.readUnsignedByte());

        // Read dummy1 (4 bytes)
        colony.setDummy1(readBytes(stream, 4));

        // Read colonists number (1 byte)
        colony.setColonistsNum(stream.readUnsignedByte());

        // Read colonists occupation (32 bytes)
        colony.setColonistsOccupation(readTableValueList(tables.getOccupations(), 32, stream::readUnsignedByte));

        // Read colonists specialization (32 bytes)
        colony.setColonistsSpecialization(readTableValueList(tables.getOccupations(), 32, stream::readUnsignedByte));

        // Read colonists time (16 bytes)
        colony.setColonistsTime(readByteList(16, stream));

        // Read tile usage (8 bytes)
        colony.setTileUsage(readByteList(8, stream));

        // Read dummy2 (12 bytes)
        colony.setDummy2(readBytes(stream, 12));

        // Read buildings bitset (6 bytes)
        colony.setBuildingsBitset(readBytes(stream, 6));

        // Read customs house (2 bytes)
        colony.setCustomsHouse(stream.readUnsignedShort());

        // Read dummy3 (6 bytes)
        colony.setDummy3(readBytes(stream, 6));

        // Read hammers (2 bytes)
        colony.setHammers(stream.readUnsignedShort());

        // Read current production (1 byte)
        colony.setCurrentProduction(stream.readUnsignedByte());

        // Read dummy4 (5 bytes)
        colony.setDummy4(readBytes(stream, 5));

        // Read storage (dynamic based on GOODS enum length)
        colony.setStorage(readShortList(0x10, stream));

        // Read dummy5 (8 bytes)
        colony.setDummy5(readBytes(stream, 8));

        // Read bells (4 bytes)
        colony.setBells(stream.readInt());

        // Read data (4 bytes)
        colony.setData(stream.readInt());

        return colony;
    }

    private Unit readUnit(Unit unit, ImageInputStream stream) throws IOException {
        // Read position
        unit.setX(stream.readUnsignedByte());
        unit.setY(stream.readUnsignedByte());

        // Read type (1 byte)
        unit.setType(readTableValue(tables.getUnits(), stream::readUnsignedByte));

        // Read nation index (1 byte)
        int nationIndex = stream.readUnsignedByte();
        unit.setNationIndex(readTableValue(tables.getNations(), () -> nationIndex & 0xf));
        unit.setDummy0((nationIndex >> 4) & 0xf);

        // Read dummy1 (1 byte)
        unit.setDummy1(stream.readUnsignedByte());

        // Read used moves (1 byte)
        unit.setUsedMoves(stream.readUnsignedByte());

        // Read dummy2 (2 bytes) - order related
        unit.setDummy2(readBytes(stream, 2));

        // Read order (1 byte)
        unit.setOrder(readTableValue(tables.getOrders(), stream::readUnsignedByte));

        // Read goto position
        unit.setGotoX(stream.readUnsignedByte());
        unit.setGotoY(stream.readUnsignedByte());

        // Read dummy3 (1 byte)
        unit.setDummy3(readBytes(stream, 1));

        // Read num cargo (1 byte)
        unit.setNumCargo(stream.readUnsignedByte());

        // Read cargo types (4 bits per cargo type, packed into 3 bits * 4 cargo = 12 bits = 2 bytes)
        // Using Bits(3, 4) means 3 bits per cargo type, 4 cargo slots
        unit.setCargoTypes(readPackedCargoTypes(stream));

        // Read cargo amount (6 bytes)
        unit.setCargoAmount(readByteList(6, stream));

        // Read dummy4 (1 byte)
        unit.setDummy4(readBytes(stream, 1));

        // Read profession (1 byte)
        unit.setProfession(readTableValue(tables.getOccupations(), stream::readUnsignedByte));

        unit.setPrevious(stream.readShort());
        unit.setNext(stream.readShort());

        return unit;
    }

    /**
     * Helper method to read packed cargo types (4 bits per cargo type, 6 cargo slots)
     */
    private List<TableValue> readPackedCargoTypes(ImageInputStream stream) throws IOException {
        List<TableValue> cargoTypes = new ArrayList<>();

        // Extract 6 values of 4 bits each
        for (int i = 0; i < 3; i++) {
            int b = stream.readUnsignedByte();
            cargoTypes.add(readTableValue(tables.getGoods(), () -> b & 0xf));
            cargoTypes.add(readTableValue(tables.getGoods(), () -> (b >> 4) & 0xf));
        }

        return cargoTypes;
    }

    private Europe readEurope(Europe europe, ImageInputStream stream) throws IOException {
        // Read padding1 (1 byte)
        europe.setPadding1(readBytes(stream, 1));

        // Read tax rate (1 byte)
        europe.setTaxRate(stream.readUnsignedByte());

        // Read next recruits (3 bytes)
        europe.setNextRecruits(readTableValueList(tables.getOccupations(), 3, () -> stream.readUnsignedByte()));

        // Read padding2 (2 bytes)
        europe.setPadding2(readBytes(stream, 2));

        // Read founding fathers bitset (4 bytes)
        europe.setFoundingFathersBitset(readBytes(stream, 4));

        // Read padding3 (1 byte)
        europe.setPadding3(readBytes(stream, 1));

        // Read current bells (2 bytes)
        europe.setCurrentBells(stream.readUnsignedShort());

        // Read padding4 (4 bytes)
        europe.setPadding4(readBytes(stream, 4));

        // Read current founding father (2 bytes)
        europe.setCurrentFoundingFather(stream.readUnsignedShort());

        // Read padding5 (10 bytes)
        europe.setPadding5(readBytes(stream, 10));

        // Read bought artillery (1 byte)
        europe.setBoughtArtillery(stream.readUnsignedByte());

        // Read padding6 (11 bytes)
        europe.setPadding6(readBytes(stream, 11));

        // Read gold (2 bytes)
        europe.setGold(stream.readUnsignedShort());

        // Read padding7 (2 bytes)
        europe.setPadding7(readBytes(stream, 2));

        // Read current crosses (2 bytes)
        europe.setCurrentCrosses(stream.readUnsignedShort());

        // Read needed crosses (2 bytes)
        europe.setNeededCrosses(stream.readUnsignedShort());

        // Read padding8 (26 bytes)
        europe.setPadding8(readBytes(stream, 26));

        // Read goods price (dynamic based on GOODS enum length)
        int goodsCount = 16;
        europe.setGoodsPrice(readByteList(goodsCount, stream));

        // Read goods unknown (dynamic based on GOODS enum length) - Short = 2 bytes each
        europe.setGoodsUnknown(readShortList(goodsCount, stream));

        // Read goods balance (dynamic based on GOODS enum length) - Int = 4 bytes each
        europe.setGoodsBalance(readIntList(goodsCount, stream));

        // Read goods demand (dynamic based on GOODS enum length) - Int = 4 bytes each
        europe.setGoodsDemand(readIntList(goodsCount, stream));

        // Read goods demand2 (dynamic based on GOODS enum length) - Int = 4 bytes each
        europe.setGoodsDemand2(readIntList(goodsCount, stream));

        return europe;
    }

    private IndianVillage readindianVillage(IndianVillage indianVillage, ImageInputStream stream) throws IOException {
        // Read position
        indianVillage.setX(stream.readUnsignedByte());
        indianVillage.setY(stream.readUnsignedByte());

        // Read nation (1 byte)
        indianVillage.setNation(readTableValue(tables.getNations(), stream::readUnsignedByte));

        // Read state (1 byte)
        indianVillage.setState(stream.readUnsignedByte());

        // Read population (1 byte)
        indianVillage.setPopulation(stream.readUnsignedByte());

        // Read mission (1 byte) - defaults to "none"
        indianVillage.setMission(stream.readUnsignedByte());

        // Read padding1 (4 bytes)
        indianVillage.setPadding1(readBytes(stream, 4));

        // Read panic (1 byte)
        indianVillage.setPanic(stream.readUnsignedByte());

        // Read padding2 (5 bytes)
        indianVillage.setPadding2(readBytes(stream, 5));

        // Read debug number (1 byte)
        indianVillage.setDebugNumber(stream.readUnsignedByte());

        // Read population loss in current turn (1 byte)
        indianVillage.setPopulationLossInCurrentTurn(stream.readUnsignedByte());

        return indianVillage;
    }

    private IndianTribe readTribe(IndianTribe indianTribe, ImageInputStream stream) throws IOException {
        // Read unk0 (1 byte)
        indianTribe.setUnk0(stream.readUnsignedByte());

        // Read unk1 (1 byte)
        indianTribe.setUnk1(stream.readUnsignedByte());

        // Read level (1 byte)
        indianTribe.setLevel(stream.readUnsignedByte());

        // Read unk2 (4 bytes)
        indianTribe.setUnk2(readBytes(stream, 4));

        // Read armed braves (1 byte)
        indianTribe.setArmedBraves(stream.readUnsignedByte());

        // Read horse herds (1 byte)
        indianTribe.setHorseHerds(stream.readUnsignedByte());

        // Read unk3 (5 bytes)
        indianTribe.setUnk3(readBytes(stream, 5));

        // Read stock (16 items, each 2 bytes = 32 bytes total)
        indianTribe.setStock(readShortList(16, stream));

        // Read unk4 (12 bytes)
        indianTribe.setUnk4(readBytes(stream, 12));

        // Read meetings (4 meetings, each 1 byte = 4 bytes total)
        indianTribe.setMeetings(readByteList(4, stream));

        // Read unk5 (8 bytes)
        indianTribe.setUnk5(readBytes(stream, 8));

        // Read aggressions (4 aggressions, each 2 bytes = 8 bytes total)
        indianTribe.setAggressions(new ArrayList<>());
        for (int i = 0; i < 4; i++) {
            IndianTribe.Aggression aggression = new IndianTribe.Aggression();
            aggression.setAggr(stream.readUnsignedByte());
            aggression.setAggrHigh(stream.readUnsignedByte());
            indianTribe.getAggressions().add(aggression);
        }

        return indianTribe;
    }

    private GameMap readGameMap(GameMap gameMap, ImageInputStream stream, Position mapSize) throws IOException {
        gameMap.setWidth(mapSize.getX());
        gameMap.setHeight(mapSize.getY());

        int mapDataSize = mapSize.getX() * mapSize.getY(); // Adjust based on actual format
        byte[] terrain = new byte[mapDataSize];
        stream.readFully(terrain);
        gameMap.setTerrain(terrain);

        byte[] surface = new byte[mapDataSize];
        stream.readFully(surface);
        gameMap.setSurface(surface);

        byte[] visitor = new byte[mapDataSize];
        stream.readFully(visitor);
        gameMap.setVisitor(visitor);

        byte[] visibility = new byte[mapDataSize];
        stream.readFully(visibility);
        gameMap.setVisibility(visibility);

        for (byte b : terrain) {
            gameMap.getTiles().add(new Tile(b & 0xff));
        }


        // calculate neighbors bitmasks
        gameMap.postProcess();

        return gameMap;
    }



    private TableValue readTableValue(List<String> table, IoSupplier<Integer> indexReader) throws IOException {
        int index = indexReader.get();
        if (index < 0 || index >= table.size()) {
            return new TableValue(index, "illegal index for table.");
        }
        return new TableValue(index, table.get(index));
    }

    private List<TableValue> readTableValueList(List<String> table, int size, IoSupplier<Integer> indexReader) throws IOException {
        List<TableValue> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(readTableValue(table, indexReader));
        }
        return result;
    }


    private boolean hasAnySectionErrors() {
        return (saveFile.getHeader() != null && saveFile.getHeader().hasError());
    }


    private void addGlobalError(String message) {
        hasGlobalErrors = true;
        saveFile.getGlobalErrors().add(message);
    }
}
